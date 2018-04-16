# tgjava

This is a program running in docker using telegram-cli to send/receive messages via MySQL database.

To register a new phone, please run:

docker run -it -v /path/to/myconfig:/root/.telegram-cli b3vis/telegram-cli

/path/to/myconfig is a directory to keep telegram-cli config or session. Please use the same directory in docker-compose.yml.

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

To send a message out, insert an entry into the outgoing table. E.g.

INSERT outgoing (msg_phone, msg_text) VALUES ('60121010101', 'This is a test message.')

To send a chat group message, please add the telegram phone number into the group and then send a message 'identify' to this chat group. You shall receive an id identifying the group. If the group id is 12345 for example, just prefix 'g' in front:

INSERT outgoing (msg_phone, msg_text) VALUES ('g12345', 'This is a test chat message.')

Incoming messages are stored into the incoming table. 
