# **Mi Proyecto de Aprendizaje con Spring Boot**

Este es un proyecto de aprendizaje en construcción que utiliza Spring Boot para crear una API REST. El objetivo de este proyecto es aprender los conceptos básicos de Spring Boot y aplicarlos para crear una API REST funcional.

## **Requisitos previos**

Antes de comenzar, es necesario tener instalado lo siguiente:

**Java 17**
**Maven**

## **Ejecución del proyecto**

**Para ejecutar el proyecto, sigue estos pasos:**

1. Clona este repositorio en tu máquina local.
2. Abre una terminal y navega al directorio del proyecto.
3. Ejecuta el siguiente comando para compilar el proyecto: mvn clean install
4. Ejecuta el siguiente comando para iniciar la aplicación: mvn spring-boot:run
5. Abre un navegador y visita la siguiente URL para acceder a la aplicación: http://localhost:8080

## **Endpoints disponibles**

La API REST ya cuenta con algunos endpoints implementados. Aquí se muestra una lista de ellos:

### **Obtener todos los usuarios**

Algunos de los endpoints
GET/api/perfiles  (Obtienes todos los perfiles)

GET/api/perfiles/{id}  (Obtienes un perfil en específico)

POST/api/perfiles  (Creas un nuevo perfil)

PUT/api/perfiles/{id}  (Editas un perfil existente)

DELETE/api/perfiles/{id}  (Eliminas un perfil existente)


## **Estructura del proyecto**

El proyecto está estructurado de la siguiente manera:

src/main/java: Contiene el código fuente de la aplicación.
src/main/resources: Contiene los archivos de configuración y los recursos de la aplicación.
src/test/java: Contiene los tests unitarios de la aplicación.

## **Licencia**
Este proyecto está bajo la Licencia MIT. Puedes leer el archivo LICENSE para obtener más información.
