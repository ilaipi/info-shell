CREATE DATABASE IF NOT EXISTS info_shell DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

use info_shell;

create table IF NOT EXISTS info_gather_flow(
 id int(10) not null primary key auto_increment,
 net_name varchar(256),
 gather_programa varchar(256),
 list_page_url varchar(8000),
 UUID varchar(64),
 version int(10),
 status varchar(16),
 updatetime datetime,
 flow_desc text,
 request_headers text,
 request_bodys text
)DEFAULT CHARACTER SET = utf8;

create table IF NOT EXISTS info_flow_task(
 id int(10) not null primary key auto_increment,
 flow_uuid varchar(64),
 cron varchar(256),
 task_name varchar(256)
)DEFAULT CHARACTER SET = utf8;

create table IF NOT EXISTS info_shop_info(
 id int(10) not null primary key auto_increment,
 uuid varchar(64),
 shop_name varchar(512),
 shop_address varchar(512),
 shop_url varchar(8000),
 shop_tel varchar(256),
 shop_city varchar(64),
 shop_recommend_dish text,
 shop_hours varchar(256),
 info_net_name varchar(256),
 info_source varchar(256),
 full_category_path_name varchar(512),
 shop_stars varchar(256),
 shop_average varchar(256),
 shop_service varchar(256),
 shop_flavour varchar(256),
 shop_env varchar(256),
 updatetime datetime
)DEFAULT CHARACTER SET = utf8;

create table IF NOT EXISTS info_common_info(
 id int(10) not null primary key auto_increment,
 info_net_name varchar(256),
 info_source varchar(256),
 info_url varchar(8000),
 title varchar(512),
 uuid varchar(64),
 updatetime datetime
)DEFAULT CHARACTER SET = utf8;

create table IF NOT EXISTS info_hotel_info(
 id int(10) not null primary key auto_increment,
 info_net_name varchar(256),
 info_source varchar(256),
 info_url varchar(8000),
 hotel_name varchar(512),
 hotel_address varchar(2000),
 hotel_tel varchar(256),
 hotel_service text,
 hotel_room text,
 hotel_city varchar(64),
 hotel_top_rank varchar(64),
 hotel_total text,
 hotel_intro text,
 hotel_transport text,
 hotel_opened varchar(512),
 uuid varchar(64),
 updatetime datetime
)DEFAULT CHARACTER SET = utf8;
