# Chat Cifrado

## Desarrollo del programa
Este proyecto consiste en la implementación de un chat cifrado entre dos instancias. Una vez establecida la conexión, se negocian las claves de cifrado empleando el algoritmo Diffie-Hellman, la clave debe ajustarse a 256 bits, para emplearla con el algoritmo AES.

### Herramientas
- **Java:** lógica del programa.
- **Maven:** herramienta de automatización de compilación, para gestionar las dependencias necesarias.
- **JavaFX:** interfaz gráfica.

### Implementación
El primer paso fue crear los paquetes para el **client** y para el **server**. Para cada caso se manejaron dos carpetas **model** y **ui**.
Ahora, dentro del **model** del **client** se encuentra la clase principal Client y la clase DiffieHellman. Mientras que, dentro de la **ui**, se encuentra la clase ClientController y el Main, estas últimas siendo necesarias para el manejo de la interfaz.
Asimismo, la carpeta **model** del **server** contiene la clase Server y la implementación del DiffieHellman. Y dentro de la **ui**, está la clase ServerController y el Main.

El segundo paso consistió en establecer la conexión entre cliente y servidor a través de Sockets. En el Servidor se específica el puerto y en el Cliente se indica la dirección del host y el puerto proporcionado por el Servidor. 

### Dificultades
Por la añadidura de una interfaz gráfica, hubo algunas complicaciones en la implmentación a cabalidad de la visión gráfica pensada para el programa, principalmente por la falta de práctica con JavaFX y CSSFX. En cuanto a la arquitecura del código, definir la estructura del proyecto fue un desafío en sí mismo, y hubo al menos 2 implementaciones anteriores a la actual.

Una vez implementada la versión actual, el mayor problema consistía en la inhabilidad de terminar correctamente el proceso de servidor y cliente, los cuales se conectan a través d eun socket en un puerto, ya que los procesos del puerto quedan activos, y con cada nueva ejecución de los dos artefactos era necesario terminar el proceso manualmente. Esto se efectuó con la ayuda de dos comandos: netstat -ano | findstr :5130, el cual busca y lista los procesos que se ejecutan en el puerto 5310; y taskkill /pid <PID> -F, comando que permite terminar los procesos en el PID especificado.

Finalmente, la ejecución de las instancias en máquinas separadas fue dificultosa por las configuraciones relaciondas con los sockets de Java, y la logistica para probar la solución desplegada en varias máquinas. No obstante, el estado actual de la solución permite tener dos instancias corriendo en 127.0.0.0, o la red de loopback, y permite la transmisión bidireccional de mensajes encriptados.

### Conclusiones
Utilizar Diffie-Hellman como método de encripción resulta útil para asegurar el intercambio de información entre dos partes, y las librerías criptográficas de Java proveen un framework robusto para hacerlo. El desarrollo brinda una experiencia de código altamente configurable, y eso causa una mayor versatilidad en la implementación, pero complica de igual manera el desarrollo de la misma. Aún así, el API criptográfico de Java es una buena alternativa para la implementación de metodos de claves simétricas como lo es Diffie-Hellman.
