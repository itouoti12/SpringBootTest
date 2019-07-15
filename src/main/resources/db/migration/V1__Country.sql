CREATE TABLE public.country (
	country_id serial NOT NULL,
	country varchar(50) NOT NULL,
	last_update timestamp NOT NULL DEFAULT now(),
	CONSTRAINT country_pkey PRIMARY KEY (country_id)
)