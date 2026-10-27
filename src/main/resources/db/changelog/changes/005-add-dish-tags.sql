--liquibase formatted sql

--changeset delivery:catalog-005-add-dish-tags
ALTER TABLE public.dishes ADD COLUMN IF NOT EXISTS tags_json TEXT;
--rollback ALTER TABLE public.dishes DROP COLUMN IF EXISTS tags_json;
