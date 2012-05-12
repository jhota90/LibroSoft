drop database mayraata;
CREATE DATABASE mayraata;
USE mayraata;

CREATE TABLE Perfil (
codigo INTEGER PRIMARY KEY,
descripcion VARCHAR (30) NOT NULL
);

INSERT INTO Perfil VALUES (1,'Jefe de Biblioteca');
INSERT INTO Perfil VALUES(2,'Bibliotecario');
INSERT INTO Perfil VALUES(3,'Encargado de Compras');
INSERT INTO Perfil VALUES(4,'Estudiante');
INSERT INTO Perfil VALUES(5,'Profesor');

CREATE TABLE Nacionalidad (
codigo INTEGER PRIMARY KEY,
nombre VARCHAR (50) NOT NULL
);

CREATE TABLE Idioma(
codigo INTEGER PRIMARY KEY,
nombre VARCHAR(15) NOT NULL
);


CREATE TABLE Carrera(
codigo INTEGER PRIMARY KEY,
nombre VARCHAR(50) NOT NULL
);

CREATE TABLE Dependencia (
codigo INTEGER PRIMARY KEY,
nombre VARCHAR(50) NOT NULL
);

CREATE TABLE Palabras_Clave (
palabra VARCHAR (15) PRIMARY KEY,
descripcion VARCHAR (50) NOT NULL
);

CREATE TABLE Editorial(
codigo INTEGER PRIMARY KEY,
nombre VARCHAR (50) NOT NULL,
pais_origen INTEGER NOT NULL,
FOREIGN KEY (pais_origen) REFERENCES Nacionalidad (codigo)
);

CREATE TABLE Autor(
codigo INTEGER PRIMARY KEY,
nombres VARCHAR (50) NOT NULL,
apellidos VARCHAR (50) NOT NULL,
codigo_nacionalidad INTEGER NOT NULL,
FOREIGN KEY (codigo_nacionalidad) REFERENCES Nacionalidad (codigo)
);

CREATE TABLE Area_de_Conocimiento (
codigo INTEGER PRIMARY KEY,
nombre VARCHAR(50) NOT NULL,
descripcion VARCHAR(50) NOT NULL
);

CREATE TABLE Subarea_de_Conocimiento (
codigo INTEGER PRIMARY KEY,
codigo_padre INTEGER NOT NULL,
FOREIGN KEY (codigo) REFERENCES Area_de_Conocimiento(codigo),
FOREIGN KEY (codigo_padre) REFERENCES Area_de_Conocimiento(codigo)
);

CREATE TABLE Libro (
isbn VARCHAR (50) PRIMARY KEY,
editorial INTEGER NOT NULL,
titulo VARCHAR(50) NOT NULL,
ano_publicacion VARCHAR(50) NOT NULL,
area_conocimiento INTEGER NOT NULL,
imagen BIT NOT NULL,
idioma INTEGER NOT NULL,
FOREIGN KEY (editorial) REFERENCES Editorial (codigo),
FOREIGN KEY (area_conocimiento) REFERENCES Area_de_Conocimiento (codigo),
FOREIGN KEY (idioma) REFERENCES Idioma (codigo)
);

CREATE TABLE Libro_Digital (
isbn VARCHAR (50) PRIMARY KEY,
formato_archivo VARCHAR (50) NOT NULL,
tamano INTEGER NOT NULL,
url VARCHAR (50) NOT NULL
);

CREATE TABLE Autores_x_Libro(
codigo INTEGER,
isbn VARCHAR(50),
PRIMARY KEY (codigo,isbn),
FOREIGN KEY (codigo) REFERENCES Autor(codigo),
FOREIGN KEY (isbn) REFERENCES Libro(isbn)
);


CREATE TABLE Palabras_x_Libro(
isbn VARCHAR(50),
palabra VARCHAR (50),
PRIMARY KEY(isbn,palabra),
FOREIGN KEY (isbn) REFERENCES Libro (isbn),
FOREIGN KEY (palabra) REFERENCES Palabras_Clave (palabra)
);

CREATE TABLE Tipo_Multa(
codigo INTEGER PRIMARY KEY,
descripcion VARCHAR (20) NOT NULL
);

CREATE TABLE Sala (
sala INTEGER PRIMARY KEY,
nombre VARCHAR(20) NOT NULL
);

CREATE TABLE Pasillo (
pasillo INTEGER PRIMARY KEY,
area_conocimiento INTEGER,
FOREIGN KEY (area_conocimiento) REFERENCES Area_de_Conocimiento (codigo)
);

CREATE TABLE Estante (
estante INTEGER PRIMARY KEY,
numero_cajones INTEGER NOT NULL
);

CREATE TABLE Cajon (
cajon INTEGER PRIMARY KEY,
estado VARCHAR(10) NOT NULL DEFAULT 'Vacio'
);

CREATE TABLE Ubicacion(
ubicacion INTEGER PRIMARY KEY,
cajon INTEGER,
estante INTEGER,
pasillo INTEGER,
sala INTEGER,
FOREIGN KEY (cajon) REFERENCES Cajon (cajon),
FOREIGN KEY (estante) REFERENCES Estante (estante),
FOREIGN KEY (pasillo) REFERENCES Pasillo (pasillo),
FOREIGN KEY (sala) REFERENCES Sala (sala)
);

CREATE TABLE Ejemplar(
codigo INTEGER PRIMARY KEY,
isbn VARCHAR(50) NOT NULL,
fecha_adquisicion DATE NOT NULL,
valor_adquisicion INTEGER NOT NULL,
estado VARCHAR(10) DEFAULT 'Disponible' NOT NULL,
ubicacion INTEGER NOT NULL,
FOREIGN KEY (ubicacion) REFERENCES Ubicacion (ubicacion)
);

CREATE TABLE Usuario (
id_usuario INTEGER PRIMARY KEY,
clave VARCHAR (15) NOT NULL,
perfil INTEGER NOT NULL,
FOREIGN KEY (perfil) REFERENCES Perfil (codigo) 
);

CREATE TABLE Estudiante (
id_estudiante INTEGER PRIMARY KEY,
nombres VARCHAR (30) NOT NULL,
apellidos VARCHAR (30) NOT NULL,
tipo_documento VARCHAR (20) NOT NULL,
numero_documento INTEGER NOT NULL,
genero VARCHAR (10) NOT NULL,
direccion VARCHAR (50) NOT NULL,
telefono VARCHAR (10) NOT NULL,
ciudad VARCHAR(20) NOT NULL,
email VARCHAR(30) NOT NULL,
universidad VARCHAR (50) NOT NULL,
carrera INTEGER NOT NULL,
FOREIGN KEY (id_estudiante) REFERENCES Usuario (id_usuario),
FOREIGN KEY (carrera) REFERENCES Carrera (codigo)
);

CREATE TABLE Empleado (
id_empleado INTEGER PRIMARY KEY,
nombres VARCHAR (50) NOT NULL,
apellidos VARCHAR (50) NOT NULL,
tipo_documento VARCHAR (5) NOT NULL,
genero VARCHAR(10) NOT NULL,
direccion VARCHAR (50) NOT NULL,
telefono VARCHAR(10) NOT NULL,
ciudad VARCHAR (50) NOT NULL,
email VARCHAR (50) NOT NULL,
universidad VARCHAR (50) NOT NULL,
FOREIGN KEY (id_empleado) REFERENCES Usuario (id_usuario)
);

CREATE TABLE Profesor (
id_profesor INTEGER PRIMARY KEY,
nombres VARCHAR(50) NOT NULL,
apellidos VARCHAR(50) NOT NULL,
tipo_documento VARCHAR(5) NOT NULL,
genero VARCHAR(10) NOT NULL,
direccion VARCHAR(50) NOT NULL,
telefono VARCHAR(10) NOT NULL,
ciudad VARCHAR(50) NOT NULL,
email VARCHAR(50) NOT NULL,
dependencia INTEGER NOT NULL,
titulo VARCHAR (50),
FOREIGN KEY (id_profesor) REFERENCES Usuario (id_usuario),
FOREIGN KEY (dependencia) REFERENCES Dependencia (codigo)
);

CREATE TABLE Area_x_Profesor (
id_profesor INTEGER,
codigo_area INTEGER,
PRIMARY KEY (id_profesor, codigo_area),
FOREIGN KEY (id_profesor) REFERENCES Profesor(id_profesor),
FOREIGN KEY (codigo_area) REFERENCES Area_de_Conocimiento (codigo)
);

CREATE TABLE Descarga (
codigo INTEGER PRIMARY KEY AUTO_INCREMENT,
id_usuario INTEGER,
fecha TIMESTAMP,
isbn VARCHAR (50),
ip VARCHAR (50),
FOREIGN KEY (id_usuario) REFERENCES Usuario (id_usuario),
FOREIGN KEY (isbn) REFERENCES Libro_Digital(isbn)
);

CREATE TABLE Multa (
codigo INTEGER PRIMARY KEY,
id_usuario INTEGER NOT NULL,
codigo_ejemplar INTEGER NOT NULL,
tipo_multa INTEGER NOT NULL,
valor INTEGER NOT NULL,
estado VARCHAR (10) NOT NULL DEFAULT 'Debe',
FOREIGN KEY (id_usuario) REFERENCES Usuario (id_usuario),
FOREIGN KEY (codigo_ejemplar) REFERENCES Ejemplar (codigo),
FOREIGN KEY (tipo_multa) REFERENCES Tipo_Multa(codigo)
);

CREATE TABLE Pago_Multa(
	codigo INTEGER PRIMARY KEY,
	fecha DATE NOT NULL,
	multa INTEGER,
	FOREIGN KEY (multa) REFERENCES Multa(codigo)
);

CREATE TABLE Devolucion(
codigo INTEGER PRIMARY KEY,
id_usuario INTEGER NOT NULL,
codigo_ejemplar INTEGER NOT NULL,
fecha DATE NOT NULL,
FOREIGN KEY (id_usuario) REFERENCES Usuario (id_usuario),
FOREIGN KEY (codigo_ejemplar) REFERENCES Ejemplar (codigo)
);

CREATE TABLE Solicitud_de_Libro(
codigo INTEGER PRIMARY KEY,
fecha DATE NOT NULL,
id_usuario INTEGER NOT NULL,
isbn VARCHAR(50) NOT NULL,
titulo VARCHAR (30) NOT NULL,
descripion VARCHAR(50) NOT NULL,
FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
);

CREATE TABLE Orden_de_Prestamo (
codigo INTEGER PRIMARY KEY,
id_usuario INTEGER NOT NULL,
fecha DATE NOT NULL,
FOREIGN KEY (id_usuario) REFERENCES Usuario (id_usuario)
);

CREATE TABLE OrdenPrestamo_x_Ejemplar (
codigo_orden_prestamo INTEGER,
codigo_ejemplar INTEGER,
PRIMARY KEY (codigo_orden_prestamo, codigo_ejemplar),
FOREIGN KEY (codigo_orden_prestamo) REFERENCES Orden_de_Prestamo (codigo),
FOREIGN KEY (codigo_ejemplar) REFERENCES Ejemplar (codigo)
);

CREATE TABLE Orden_de_Entrega (
codigo INTEGER PRIMARY KEY,
id_empleado INTEGER NOT NULL,
codigo_prestamo INTEGER NOT NULL,
fecha_Entrega DATE NOT NULL,
fecha_devolucion DATE NOT NULL,
FOREIGN KEY (codigo_prestamo) REFERENCES Orden_de_Prestamo (codigo)
);

CREATE TABLE Prestamo (
codigo_ejemplar INTEGER,
codigo_entrega INTEGER,
PRIMARY KEY (codigo_ejemplar, codigo_entrega),
FOREIGN KEY (codigo_ejemplar) REFERENCES Ejemplar (codigo),
FOREIGN KEY (codigo_entrega) REFERENCES Orden_de_Entrega (codigo)
);
