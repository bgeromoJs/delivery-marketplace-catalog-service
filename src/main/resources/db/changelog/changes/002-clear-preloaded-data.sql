--liquibase formatted sql

--changeset delivery:catalog-002-clear-preloaded-data
DELETE FROM public.dishes;
--rollback -- No data restoration is provided for intentionally cleared catalog records.
