CREATE TABLE IF NOT EXISTS repositories(
    id UUID NOT NULL,
    repository VARCHAR(255) UNIQUE NOT NULL,
    owner VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users(
    id UUID NOT NULL,
    slack_user_id VARCHAR(255) UNIQUE NOT NULL,
    slack_channel VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS subscriptions(
    user_id UUID NOT NULL,
    repository_id UUID NOT NULL,
    type VARCHAR(10) NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, repository_id),
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
            REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_repository
        FOREIGN KEY (repository_id)
            REFERENCES repositories(id) ON DELETE CASCADE
);