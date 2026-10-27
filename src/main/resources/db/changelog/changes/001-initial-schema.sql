--liquibase formatted sql

--changeset delivery:catalog-001-create-dishes
CREATE TABLE IF NOT EXISTS public.dishes (
    id UUID PRIMARY KEY NOT NULL,
    tenant_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    price NUMERIC(38, 2) NOT NULL,
    category VARCHAR(255) NOT NULL,
    image_url VARCHAR(255),
    available BOOLEAN,
    options_json TEXT,
    preparation_time_min INTEGER,
    created_at TIMESTAMP WITHOUT TIME ZONE
);
--rollback DROP TABLE public.dishes;
