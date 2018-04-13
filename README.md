# tgjava

This is a program running in docker using telegram-cli to send/receive messages via MySQL database.

To register a new phone, please run:

docker run -it -v key:/root/.telegram-cli b3vis/telegram-cli

"key" is a directory to keep telegram-cli config or session.

After that we may run the program using "docker-compose up -d" using the docker-compose.yml file in the docker folder.

The docker-compose.yml file used to run/setup the MySQL database is inside the docker/mysql folder.

Then we use the following SQL to create tables in a database called "telegram" in MySQL database:

CREATE TABLE IF NOT EXISTS contacts (
contact_id int(11) NOT NULL AUTO_INCREMENT,
phone varchar(50) COLLATE utf8_unicode_ci NOT NULL,
user_id int(11) NOT NULL,
nickname varchar(50) COLLATE utf8_unicode_ci NOT NULL,
PRIMARY KEY (contact_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS incoming (
msg_id int(11) NOT NULL,
msg_phone varchar(50) COLLATE utf8_unicode_ci NOT NULL,
msg_text varchar(1000) COLLATE utf8_unicode_ci NOT NULL,
msg_rid int(11) NOT NULL DEFAULT '0',
msg_status int(11) NOT NULL DEFAULT '0',
msg_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (msg_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS outgoing (
msg_id int(11) NOT NULL AUTO_INCREMENT,
msg_phone varchar(50) COLLATE utf8_unicode_ci NOT NULL,
msg_text varchar(1000) COLLATE utf8_unicode_ci NOT NULL,
msg_status int(11) NOT NULL DEFAULT '0',
msg_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (msg_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;
