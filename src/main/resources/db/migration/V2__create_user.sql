create table user
(
    id                  bigint auto_increment
        primary key,
    username            nvarchar(50)  not null,
    password            nvarchar(255) null,
    email               nvarchar(30)  not null,
    name                nvarchar(20)  null,
    gender              nvarchar(10)  null,
    birth               date null,
    role                nvarchar(10)  not null,
    status              nvarchar(20)  not null,
    active_area         nvarchar(50)  null,
    platform            nvarchar(10)  not null,
    logged_in_at        datetime null,
    login_fail_count    int not null,
    password_changed_at datetime null,
    created_at          datetime null,
    updated_at          datetime null
);