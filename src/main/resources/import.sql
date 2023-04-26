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