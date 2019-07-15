CREATE TABLE public.city (
	city_id serial NOT NULL,
	city varchar(50) NOT NULL,
	country_id int NOT NULL,
	last_update timestamp NOT NULL DEFAULT now(),
	CONSTRAINT city_pkey PRIMARY KEY (city_id),
	CONSTRAINT fk_city FOREIGN KEY (country_id) REFERENCES country(country_id)
)