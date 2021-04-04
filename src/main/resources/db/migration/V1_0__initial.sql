CREATE TABLE T_SHARE(
    id                      SERIAL NOT NULL PRIMARY KEY,
    site_id                 BIGINT NOT NULL,
    story_id                BIGINT NOT NULL,
    telegram_message_id     BIGINT,
    telegram_chat_id        VARCHAR(100) NOT NULL,
    share_date_time         TIMESTAMPTZ NOT NULL,
    success                 BOOLEAN,
    error_code              INT,
    error_description       TEXT
);
