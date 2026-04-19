create table users (
    id bigserial primary key,
    email varchar(255) not null,
    password_hash varchar(255) not null,
    full_name varchar(255) not null,
    status varchar(50) not null,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    constraint uq_users_email unique (email)
);

create table roles (
    id bigserial primary key,
    code varchar(100) not null,
    name varchar(150) not null,
    constraint uq_roles_code unique (code)
);

create table restaurants (
    id bigserial primary key,
    name varchar(255) not null,
    slug varchar(255) not null,
    status varchar(50) not null,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    constraint uq_restaurants_slug unique (slug)
);

create table restaurant_users (
    id bigserial primary key,
    restaurant_id bigint not null references restaurants (id),
    user_id bigint not null references users (id),
    membership_status varchar(50) not null,
    joined_at timestamp with time zone not null default now(),
    constraint uq_restaurant_users_restaurant_user unique (restaurant_id, user_id)
);

create table restaurant_user_roles (
    id bigserial primary key,
    restaurant_user_id bigint not null references restaurant_users (id),
    role_id bigint not null references roles (id),
    assigned_at timestamp with time zone not null default now(),
    constraint uq_restaurant_user_roles_membership_role unique (restaurant_user_id, role_id)
);

create table restaurant_tables (
    id bigserial primary key,
    restaurant_id bigint not null references restaurants (id),
    table_number varchar(50) not null,
    name varchar(255),
    capacity integer,
    status varchar(50) not null,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    constraint uq_restaurant_tables_restaurant_table_number unique (restaurant_id, table_number)
);

create table table_qr_codes (
    id bigserial primary key,
    restaurant_table_id bigint not null references restaurant_tables (id),
    token varchar(255) not null,
    qr_image_url varchar(500),
    is_active boolean not null default true,
    created_at timestamp with time zone not null default now(),
    expires_at timestamp with time zone,
    constraint uq_table_qr_codes_token unique (token)
);

create unique index uq_table_qr_codes_active_per_table
    on table_qr_codes (restaurant_table_id)
    where is_active = true;

create table menu_categories (
    id bigserial primary key,
    restaurant_id bigint not null references restaurants (id),
    name varchar(255) not null,
    display_order integer not null default 0,
    is_active boolean not null default true,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    constraint uq_menu_categories_restaurant_name unique (restaurant_id, name)
);

create table products (
    id bigserial primary key,
    restaurant_id bigint not null references restaurants (id),
    category_id bigint not null references menu_categories (id),
    name varchar(255) not null,
    description text,
    price numeric(12, 2) not null,
    is_active boolean not null default true,
    is_visible_to_customer boolean not null default true,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now()
);

create index idx_products_restaurant_id on products (restaurant_id);
create index idx_products_category_id on products (category_id);

create table orders (
    id bigserial primary key,
    restaurant_id bigint not null references restaurants (id),
    restaurant_table_id bigint not null references restaurant_tables (id),
    created_by_restaurant_user_id bigint not null references restaurant_users (id),
    status varchar(50) not null,
    total_amount numeric(12, 2) not null default 0,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    submitted_at timestamp with time zone
);

create index idx_orders_restaurant_id on orders (restaurant_id);
create index idx_orders_restaurant_table_id on orders (restaurant_table_id);
create index idx_orders_created_by_restaurant_user_id on orders (created_by_restaurant_user_id);

create unique index uq_orders_single_active_order_per_table
    on orders (restaurant_table_id)
    where status in ('DRAFT', 'RECEIVED', 'PREPARING', 'READY');

create table order_items (
    id bigserial primary key,
    order_id bigint not null references orders (id) on delete cascade,
    product_id bigint not null references products (id),
    product_name_snapshot varchar(255) not null,
    unit_price numeric(12, 2) not null,
    quantity integer not null,
    note varchar(500),
    created_at timestamp with time zone not null default now()
);

create index idx_order_items_order_id on order_items (order_id);
create index idx_order_items_product_id on order_items (product_id);

create table songs (
    id bigserial primary key,
    restaurant_id bigint not null references restaurants (id),
    title varchar(255) not null,
    artist varchar(255),
    duration_seconds integer,
    source_reference varchar(500),
    is_active boolean not null default true,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now()
);

create index idx_songs_restaurant_id on songs (restaurant_id);

create table playlists (
    id bigserial primary key,
    restaurant_id bigint not null references restaurants (id),
    name varchar(255) not null,
    status varchar(50) not null,
    created_by_restaurant_user_id bigint not null references restaurant_users (id),
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    constraint uq_playlists_restaurant_name unique (restaurant_id, name)
);

create table playlist_songs (
    id bigserial primary key,
    playlist_id bigint not null references playlists (id) on delete cascade,
    song_id bigint not null references songs (id),
    position integer not null,
    is_active boolean not null default true,
    created_at timestamp with time zone not null default now(),
    constraint uq_playlist_songs_playlist_song unique (playlist_id, song_id),
    constraint uq_playlist_songs_playlist_position unique (playlist_id, position)
);

create table playback_queues (
    id bigserial primary key,
    restaurant_id bigint not null references restaurants (id),
    status varchar(50) not null,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    constraint uq_playback_queues_restaurant unique (restaurant_id)
);

create table playback_queue_items (
    id bigserial primary key,
    playback_queue_id bigint not null references playback_queues (id) on delete cascade,
    song_id bigint not null references songs (id),
    source_type varchar(50) not null,
    source_reference_id bigint,
    position integer not null,
    status varchar(50) not null,
    queued_at timestamp with time zone not null default now(),
    constraint uq_playback_queue_items_queue_position unique (playback_queue_id, position)
);

create table playback_sessions (
    id bigserial primary key,
    restaurant_id bigint not null references restaurants (id),
    playlist_id bigint not null references playlists (id),
    playback_queue_id bigint not null references playback_queues (id),
    current_song_id bigint references songs (id),
    status varchar(50) not null,
    current_song_started_at timestamp with time zone,
    current_song_ends_at timestamp with time zone,
    voting_grace_ends_at timestamp with time zone,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    constraint uq_playback_sessions_restaurant unique (restaurant_id)
);

create table voting_rounds (
    id bigserial primary key,
    restaurant_id bigint not null references restaurants (id),
    playback_session_id bigint not null references playback_sessions (id),
    playlist_id bigint not null references playlists (id),
    status varchar(50) not null,
    created_by_type varchar(50) not null,
    created_by_restaurant_user_id bigint references restaurant_users (id),
    opened_at timestamp with time zone not null default now(),
    scheduled_close_at timestamp with time zone not null,
    closed_at timestamp with time zone,
    winning_song_id bigint references songs (id),
    tie_break_applied boolean not null default false
);

create index idx_voting_rounds_restaurant_id on voting_rounds (restaurant_id);
create index idx_voting_rounds_playback_session_id on voting_rounds (playback_session_id);

create table voting_round_candidate_songs (
    id bigserial primary key,
    voting_round_id bigint not null references voting_rounds (id) on delete cascade,
    song_id bigint not null references songs (id),
    candidate_no smallint not null,
    selection_source varchar(50) not null,
    created_at timestamp with time zone not null default now(),
    constraint uq_voting_round_candidate_songs_round_song unique (voting_round_id, song_id),
    constraint uq_voting_round_candidate_songs_round_slot unique (voting_round_id, candidate_no)
);

create table customer_sessions (
    id bigserial primary key,
    restaurant_id bigint not null references restaurants (id),
    restaurant_table_id bigint not null references restaurant_tables (id),
    table_qr_code_id bigint not null references table_qr_codes (id),
    session_token varchar(255) not null,
    status varchar(50) not null,
    started_at timestamp with time zone not null default now(),
    expires_at timestamp with time zone not null,
    last_seen_at timestamp with time zone,
    constraint uq_customer_sessions_session_token unique (session_token)
);

create index idx_customer_sessions_restaurant_table_id on customer_sessions (restaurant_table_id);

create table votes (
    id bigserial primary key,
    voting_round_id bigint not null references voting_rounds (id),
    song_id bigint not null references songs (id),
    customer_session_id bigint not null references customer_sessions (id),
    restaurant_table_id bigint not null references restaurant_tables (id),
    created_at timestamp with time zone not null default now(),
    constraint uq_votes_customer_session_round unique (customer_session_id, voting_round_id)
);

create index idx_votes_voting_round_id on votes (voting_round_id);
create index idx_votes_song_id on votes (song_id);
create index idx_votes_restaurant_table_id on votes (restaurant_table_id);

insert into roles (code, name) values
    ('RESTAURANT_ADMIN', 'Restaurant Admin'),
    ('WAITER', 'Waiter'),
    ('MUSIC_MANAGER', 'Music Manager');
