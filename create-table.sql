CREATE TABLE user_queue
(
    id         UInt64,
    fullName   String,
    updateTime UInt64
) ENGINE = RabbitMQ SETTINGS
    rabbitmq_host_port = 'rabbitmq-test-local:5672',
    rabbitmq_exchange_name = 'entity-queue-exchange',
    rabbitmq_queue_base = 'user-queue',
    rabbitmq_routing_key_list = 'user',
    rabbitmq_queue_consume = true,
    rabbitmq_exchange_type = 'direct',
    rabbitmq_format = 'JSONEachRow',
    rabbitmq_username = 'guest',
    rabbitmq_password = 'guest',
    rabbitmq_num_consumers = 5;

CREATE TABLE user
(
    id         UInt64,
    fullName   String,
    updateTime UInt64
)
    ENGINE = ReplacingMergeTree() ORDER BY (id, updateTime);


CREATE MATERIALIZED VIEW consumer TO user
AS
SELECT id, fullName, updateTime
FROM user_queue;

create table user_daily_history
(
    id             UInt64,
    fullName       String,
    lastUpdateTime UInt64,
    date           DATE
) engine ReplacingMergeTree(lastUpdateTime) order by (id, date);

create materialized view user_daily_history_consumer TO user_daily_history as
select id, fullName, updateTime as lastUpdateTime, toDate(updateTime / 1000, 'Asia/Nicosia') as date
from user_queue;


CREATE TABLE transfer_queue
(
    customerId UInt64,
    amount     UInt64,
    currency   String,
    updateTime UInt64
) ENGINE = RabbitMQ SETTINGS
    rabbitmq_host_port = 'rabbitmq-test-local:5672',
    rabbitmq_exchange_name = 'entity-queue-exchange',
    rabbitmq_queue_base = 'transfer-queue',
    rabbitmq_routing_key_list = 'transfer',
    rabbitmq_queue_consume = true,
    rabbitmq_exchange_type = 'direct',
    rabbitmq_format = 'JSONEachRow',
    rabbitmq_username = 'guest',
    rabbitmq_password = 'guest',
    rabbitmq_num_consumers = 5;

CREATE TABLE transfer
(
    customerId UInt64,
    amount     UInt64,
    currency   String,
    updateTime UInt64
)
    ENGINE = ReplacingMergeTree() ORDER BY (updateTime);

CREATE MATERIALIZED VIEW transfer_consumer TO transfer
AS
SELECT * FROM transfer_queue;