create table item(
	id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	nome varchar(100) not null,
	tipo varchar(100) not null,
	unidade_medida varchar(100) not null
);

create table fichaTecnica(
	id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	nome varchar(100) not null,
	rendimento DOUBLE PRECISION not null,
	unidade_rendimento varchar(100) not null
);

create table ingredienteFicha(
	id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	ficha_id int,
	item_id int,
	quantidade DOUBLE PRECISION not null,
	foreign key (ficha_id) references fichaTecnica(id),
	foreign key (item_id) references item(id)
);

create table lote(
	id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	item_id int,
	quantidade DOUBLE PRECISION not null,
	data_producao TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
	data_entrada TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
	data_validade DATE DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (item_id) REFERENCES item(id)
);

create table itemProcessamento(
	id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	lote_id int,
	tipo varchar(100) not null,
	quantidade DOUBLE PRECISION not null,
	FOREIGN KEY (lote_id) REFERENCES lote(id)
);


create table processamento(
	id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	data_hora TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
	processo varchar(100) not null,
	rendimento DOUBLE PRECISION not null
);