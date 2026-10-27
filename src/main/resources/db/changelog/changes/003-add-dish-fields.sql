--liquibase formatted sql

--changeset delivery:catalog-003-add-dish-fields
ALTER TABLE public.dishes ADD COLUMN IF NOT EXISTS sales_days_json TEXT;
ALTER TABLE public.dishes ADD COLUMN IF NOT EXISTS sales_windows_json TEXT;
--rollback ALTER TABLE public.dishes DROP COLUMN IF EXISTS sales_days_json; ALTER TABLE public.dishes DROP COLUMN IF EXISTS sales_windows_json;
