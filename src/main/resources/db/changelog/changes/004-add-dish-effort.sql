--liquibase formatted sql

--changeset delivery:catalog-004-add-dish-effort
ALTER TABLE public.dishes ADD COLUMN IF NOT EXISTS effort_minutes INTEGER NOT NULL DEFAULT 20;
--rollback ALTER TABLE public.dishes DROP COLUMN IF EXISTS effort_minutes;
