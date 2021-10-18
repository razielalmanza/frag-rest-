# Fragmentos REST

![ver](https://img.shields.io/badge/fragmentos-v.1.0.0-green)

## Presentación

Este proyecto  cubre la necesidad de reemplazar la actual plataforma de Fragmentos (que se encontraban en un solo proyecto de Spring utilizando *.jsp*). 

Esta nueva plataforma se divide en dos: el repositorio del Cliente, y este repositorio en el que se encuentra el __backend__ , el cual está compuesto por servicios [RESTful](https://www.ibm.com/developerworks/ssa/library/ws-restful/index.html) para obtener y actualizar los datos de la base de datos.

## Comenzando 🚀

El backend de Fragmentos corre con ayuda del framework [Spring Boot versión 2.1.5](https://spring.io/projects/spring-boot) y las dependencias provienen de [Maven Repository](https://mvnrepository.com/)
que están definidas en el archivo _pom.xml_

## **Pre-requisitos** 📋

Las herramientas necesarias para el desarrollo del proyecto son las siguientes. 

* **[Spring Tools](https://spring.io/tools)** - El framework Java utilizado (antes SpringSource Tool Suite).

* **[Java JDK 13](https://www.oracle.com/java/technologies/javase-jdk13-downloads.html)** La versión del lenguaje utilizado para la plataforma. En pruebas anteriores se ha revisado que se ha podido correr el proyecto con la version 11 y [12](https://www.oracle.com/java/technologies/javase/jdk12-archive-downloads.html) de este lenguaje.

* **[git y maven](https://git-scm.com/downloads)** - Si se desarrolla en ambiente Windows son necesarios estos comandos básicos de shell. Ver [Instalación de Git](https://dgac-conti.atlassian.net/l/c/SeGVPwCY)

* **[MySQL](https://dev.mysql.com/downloads/)** - El RDBMS utilizado.

* **[Docker](https://www.docker.com/)** Herramienta que automatiza el despliegue de aplicaciones dentro de contenedores de software.

Opcionales: 

* **[MySQL Workbench](https://www.mysql.com/products/workbench/)** Es una interfaz gráfica para poder interactuar con el contenido de la base de datos. 

* **[SSH](https://www.ssh.com/)** Software que hace posible el acceso remoto a un servidor por medio de un canal seguro. 




### **Preparación del ambiente en Windows**

Se recomienda el uso del Package Manager [Chocolatey](https://chocolatey.org/install)

Debido a limitaciones comerciales del JDK de Oracle se utilza OpenJDK en este pequeño script, no debería causar diferencias. [Versión de Oracle](https://www.oracle.com/java/technologies/javase-jdk13-downloads.html).

También mencionar que como los servidores MySQL utilizados son remotos basta usar el MySQL Workbench para conectar a la base de este proyecto. 


```bash
$ choco install openjdk12
$ choco install git
$ choco install maven
$ choco install mysql
$ choco install mysql.workbench
$ choco install docker-desktop
```
Debido al uso obsoleto de versiones en Chocolatey, se recomienda instalar Spring Tools 4 directamente en el [sitio oficial](https://spring.io/tools).
## **Instalación** 🔧

Clonar el repositorio:

```
$ git clone git@132.247.164.43:filmo/frag-rest.git
```

La rama master siempre debe respetarse para ser la versión final de cada actualización.
Por otro lado _dev_ debe ser la versión para juntar los cambios del equipo 
mientras se termina una nueva versión. Por lo tanto crea una rama a partir de _dev_.

```
$ git checkout -b <nombre_de_la_rama>
```

## **Variables de entorno**

Una vez creada la nueva rama, debes buscar el archivo application.properties en 
la ruta _src/main/resources/application.properties_ y verificar lo siguiente:
- Que la variable _spring.profiles.active_ tenga el valor **_dev_**

La configuración del ambiente debe mejorarse ya que estas 2 variables deben cambiarse antes de hacer el [build](#build) para [**Despliegue**](#deploy) en producción.

## **Estructura del proyecto** 📂

- 📂 **target**
  En esta carpeta se genera el archivo _frag-rest-0.0.1-SNAPSHOT.jar_en la fase de [**Compilación**](#compile)
para el [**Despliegue**](#deploy) en ambiente de desarrollo o producción.

- 📂 **src/main/resources**
  Aquí se encuentra el archivo _application.properties_ donde se definen las [**variables de entorno**](#dev).

- 📂 **src/main/java**
  Contiene todo el código fuente del proyecto y está organizado en los siguientes paquetes 

    - 📦 **dgac** Aquí va la clase principal FragRestApplication.java

    - 📦 **dgac.seguridad.\*** Todo lo que se encuentra aquí tiene el propósito para la acreditacion de los usuarios para tener acceso a los endpoint y realizar modificaciones y consultas en la base de datos. La técnologia utilizada para este proceso es [JWT](https://jwt.io/). 

    - 📦 **dgac.fragmentos.utilerias** Se encuentran metodos para manejar algunas caracteristicas no
    esenciales del proyecto  (Formatos o maneras en las que se realizan cálculos sobre el material fragmentos y segmentos).

    - 📦 **dgac.fragmentos.utileriaAcervo** Contiene clases para poder manejar elementos del Acervo.

    - 📦 **dgac.fragmentos** Los paquetes dentro de esta ruta contienen la parte esencial de proyecto que se encarga de realizar la comunicación REST con el frontend. 

      - 📦 **dgac.fragmentos.config** Contiene clases para poder configurar el correcto manejo de seguridad al hacer peticiones y tener control de las bases de datos a las que nos conectamos.

      - 📦 **dgac.fragmentos.entidades** Contiene los beans modelados donde los atributos de este objetos estarán relacionados con la informacion de la base de datos.
      
      - 📦 **dgac.fragmentos.dao** Se encuentran diferentes operaciones realizando operaciones SQL con el fin de llenar los tributos del objeto para que pueda ser utoilizado segun las necesidades del desarrollador.  

      - 📦 **dgac.fragmentos.servicios** Se encarga de ser el intermediarios entre los controladores y los DAO para estructurar las tareas requeridas segun la peticion REST. 
  
      - 📦 **dgac.fragmentos.controller** Contiene la clase en donde se manejan los metodos para poder realizar la comunicación REST con el frontend a partir de peticiones http.



<a name="compile"></a>

## **Compilación (build)**

Para hacer el build primero hay que verificar que las variables de entorno 
apunten a [dev](#dev) si es para ambiente de _pruebas_.
En caso de hacer un build para _producción_ se debe verificar lo siguiente:
- Que la variable _spring.profiles.active_ tenga el valor **_prod_**

- _ruta.storage_ sea igual a safe-holder (**Por revisar para fragmentos**)

Una vez verificado lo anterior el build se hace con el comando:
```bash
mvn build clean install -DskipTests
```
 desde la raíz del proyecto y se puede 
continuar con la creación de la imagen de Docker para el despliegue.

<a name="deploy"></a>

## **Despliegue**📦

Para desplegar la aplicación se usa Docker. El build de la imagen 
puede hacerse para ambiente de _pruebas_ y de _producción_.

### **Puerto**

El puerto asignado para esta aplicación es el **8094** y está definido en _application.properties_


## **Ambiente de Desarrollo**

Para hacer deploy en el ambiente de pruebas de Docker se debe generar la imagen 
con la IP donde este alojado el servidor al momento del build.

En este momento se encuentra en una IP pública.

```
$ docker build -t safe-holder/frag-rest-dev:version .
```

ó cuando este en una red privada, este deberá ser el comando:

```
$ docker build -t safe-holer/frag-rest-dev:version .
```
donde _version_ es de tipo _v1.0.0_

$ Al terminar el build se debe subir la imagen al servidor.

```
docker push <ip_del_servidor>:5000/frag-rest-dev
```

Para hacer el deploy consulte la información de Docker en la [Wiki interna]().

## Ambiente de Producción

Para hacer el build de la imagen en producción lo más importa es revisar que 
se hizo bien el maven [build](#build)

Para estas imágenes se maneja un nombre de la imagen y su versión.
**Este tag cambiará en cada nueva actualización**.

Se construye la imagen con el comando:

```
docker build -t safe-holder:/frag-rest:<version> .
```

Se sube la imagen al servidor de producción de Docker.

```
docker push safe-holder/frag-rest:<version>
```

Para hacer el deploy consulte la información de Docker en la [Wiki interna]().


## **Autores** ✒️

- **Luis Felipe Maciel** - <l_f_mm@hotmail.com>
- **Luis Bernabe** - <luis_berna@ciencias.unam.mx>
- **Raziel Almanza** <razielalmanza@ciencias.unam.mx>


