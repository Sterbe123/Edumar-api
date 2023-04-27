INSERT INTO roles (rol) VALUES ('ROLE_ADMINISTRADOR');
INSERT INTO roles (rol) VALUES ('ROLE_TRABAJADOR');
INSERT INTO roles (rol) VALUES ('ROLE_CLIENTE');

INSERT INTO usuarios (email,contrasena,estado,verificacion,rol_id,create_at,update_at,check_at) VALUES ('rodrigo@gmail.com','$2a$10$t5z9vYOlRBVAc8GS5nFMMuLktYmYtd1fNsI3g7qwEnwjmhyoxfsQG',1,1,1,NOW(),null,NOW());

INSERT INTO perfiles (run,nombre,apellido_paterno,apellido_materno,contacto,usuario_id,create_at,update_at) VALUES ("18580892-0","Rodrigo","Lazo","Fredes","991676062",1,NOW(),null);

INSERT INTO direcciones (quien_recibe,region_id,comuna_id,poblacion,calle,numero,principal,perfiles_id,create_at) VALUES ("Rodrigo Lazo",4,10,"La Aguada Centro","El Litre","433",1,1,NOW());

INSERT INTO perfiles_direcciones (perfil_id,direcciones_id) VALUES (1,1);

INSERT INTO categorias (nombre,estado) VALUE ('Filtros',1);
INSERT INTO categorias (nombre,estado) VALUE ('Aceites',1);
INSERT INTO categorias (nombre,estado) VALUE ('Lubricantes',1);
INSERT INTO categorias (nombre,estado) VALUE ('Neumaticos',1);

INSERT INTO productos (nombre,descripcion,precio,stock,categoria_id,codigo_barra,codigo_interno,create_at,update_at) VALUES ('Aceite 50-20w','Aceite para vehiculos bencinero 5L','20.000',10,2,'00124454','ace1445',NOW(),null);
INSERT INTO productos (nombre,descripcion,precio,stock,categoria_id,codigo_barra,codigo_interno,create_at,update_at) VALUES ('Aceite 25-20w','Aceite para vehiculos bencinero 1L','14.000',6,2,'00134454','ace1345',NOW(),null);
INSERT INTO productos (nombre,descripcion,precio,stock,categoria_id,codigo_barra,codigo_interno,create_at,update_at) VALUES ('Liquido freno','liquido de freno','6.990',56,3,'00144454','ace9545',NOW(),null);
INSERT INTO productos (nombre,descripcion,precio,stock,categoria_id,codigo_barra,codigo_interno,create_at,update_at) VALUES ('Filtro de aire','Filtros para Nissan','15.990',2,1,'00154454','fil1447',NOW(),null);
INSERT INTO productos (nombre,descripcion,precio,stock,categoria_id,codigo_barra,codigo_interno,create_at,update_at) VALUES ('Aceite 50-20w','Aceite para vehiculos bencinero','20.000',10,2,'00164454','ace9696',NOW(),null);
INSERT INTO productos (nombre,descripcion,precio,stock,categoria_id,codigo_barra,codigo_interno,create_at,update_at) VALUES ('Aceite 50-20w','Aceite para vehiculos bencinero','20.000',10,2,'00174454','ace1447',NOW(),null);
INSERT INTO productos (nombre,descripcion,precio,stock,categoria_id,codigo_barra,codigo_interno,create_at,update_at) VALUES ('Aceite 50-20w','Aceite para vehiculos bencinero','20.000',10,2,'00184454','ace1448',NOW(),null);
INSERT INTO productos (nombre,descripcion,precio,stock,categoria_id,codigo_barra,codigo_interno,create_at,update_at) VALUES ('Aceite 50-20w','Aceite para vehiculos bencinero','20.000',10,2,'00194454','ace1449',NOW(),null);
INSERT INTO productos (nombre,descripcion,precio,stock,categoria_id,codigo_barra,codigo_interno,create_at,update_at) VALUES ('Aceite 50-20w','Aceite para vehiculos bencinero','20.000',10,2,'00204454','ace1456',NOW(),null);
INSERT INTO productos (nombre,descripcion,precio,stock,categoria_id,codigo_barra,codigo_interno,create_at,update_at) VALUES ('Aceite 50-20w','Aceite para vehiculos bencinero','20.000',10,2,'00214454','ace1457',NOW(),null);
INSERT INTO productos (nombre,descripcion,precio,stock,categoria_id,codigo_barra,codigo_interno,create_at,update_at) VALUES ('Aceite 50-20w','Aceite para vehiculos bencinero','20.000',10,2,'00224454','ace2245',NOW(),null);
INSERT INTO productos (nombre,descripcion,precio,stock,categoria_id,codigo_barra,codigo_interno,create_at,update_at) VALUES ('Aceite 50-20w','Aceite para vehiculos bencinero','20.000',10,2,'00234454','ace2345',NOW(),null);
INSERT INTO productos (nombre,descripcion,precio,stock,categoria_id,codigo_barra,codigo_interno,create_at,update_at) VALUES ('Aceite 50-20w','Aceite para vehiculos bencinero','20.000',10,2,'00244454','ace2445',NOW(),null);
INSERT INTO productos (nombre,descripcion,precio,stock,categoria_id,codigo_barra,codigo_interno,create_at,update_at) VALUES ('Aceite 50-20w','Aceite para vehiculos bencinero','20.000',10,2,'00254454','ace2545',NOW(),null);