### Descripción
**ZXBrowser** es una aplicación para explorar, descargar y ejecutar el contenido de la base de datos de Spectrum ZXDB.

### ¿Por qué?
De vez en cuando me apetece recordar los viejos juegos del Spectrum y, por supuesto, todos los nuevos que van saliendo
(que últimamente no son pocos...).

Personalmente tengo una réplica del viejo FTP de WoS y, cuando quiero jugar a algo, a
veces me cuesta encontrar el fichero. Luego, en el caso más sencillo, tengo que arrastrar el ZIP al emulador para
ejecutarlo... pero no siempre es así. A veces tienes que buscar el fichero desde el propio emulador y otras, además
tienes que descomprimirlo porque no lee directamente los ficheros comprimidos.

Por eso decidí que necesitaba algo como ZXBrowser.

La verdad es que la idea viene de lejos. De hecho tengo una vieja versión por ahí que cargaba los datos del fichero
**rainbow-wos.zip** (alojado en WoS), pero eso es otra historia...

Ahora tengo una versión medianamente decente que me gustaría compartir con todos los amantes del Spectrum y espero que
les guste y les sea tan útil como a mí.

### Requisitos
Se necesita Java 11 (aunque a mí me funciona con la versión 1.8 de Oracle) y JavaFX.
De momento hay dos versiones:
- [ZXBrowser.jar](https://github.com/area51bis/zxbrowser/blob/main/bin/ZXBrowser.zip): Requiere tener instalado JavaFX.
- [ZXBrowser-win.jar](https://github.com/area51bis/zxbrowser/blob/main/bin/ZXBrowser-win.zip): Versión para Windows que incluye JavaFX.

### Instrucciones
El programa es bastante sencillo por ahora. Basta con ejecutarlo, descargar la base datos de ZXDB y comenzar a explorar.

La ventana muestra a la izquierda un _**arbol de categorías**_ que permite explorar por género, año, etc.

Al seleccionar una categoría, se mostrará a la derecha un listado con todas las entradas correspondientes. Al
seleccionar una entrada, se mostrarán debajo las descargas disponibles.

Al pulsar botón derecho sobre una descarga se mostrarán las opciones disponibles:
- Descargar: Si no está descargado.
- Abrir con...: Para abrir con un programa en concreto.

Al hacer doble clic, automáticamente se descargará (si es necesario) y se ejecutará con el programa asignado por
defecto.

A la derecha de las descargas se muestran las imágenes si están descargadas.

También hay una caja de texto arriba para buscar rápidamente **en la categoría seleccionada**.

### Configurar programas
Lo siento pero, por ahora, la configuración es totalmente manual. Más adelante se podrá configurar todo desde la
aplicación.

Cada programa es un fichero **JSON** dentro del directorio _progs_

Se incluyen tres ficheros de ejemplo:
- zxspin.json y zesarux.json para Windows
- fuse.json para Mac

Para que funcionen correctamente hay que modificarlos con la ruta correcta al programa para que funcionen. El formato es
el siguiente: 

zesarux.json:
```
{
    "order": 2,
    "id": "zesarux",
    "name": "ZEsarUX",
    "path": "D:\\emu\\spectrum\\zesarux\\zesarux.exe",
    "args": "--realloadfast --realtape ${filePath}",
    "ext": ["tzx", "tap", "z80"],
    "unzip": true
}
```
- **order**: Es el orden del emulador en el menú "Abrir con..."
- **id**: Identificador único para el programa.
- **name**: Nombre que se muestra en el menú.
- **path**: Ruta completa al ejecutable.
- **args**: Argumentos que se le pasarán al programa. Aquí se pueden indicar variables en la forma _${nombre_de_variable}_,
    pero actualmente sólo soporta una: _${filePath}_ que indica el nombre completo a la descarga.
- **ext**: Lista de extensiones que soporta el programa.
- **unzip**: Este parámetro es opciones e indica que necesita descomprimirse antes de ejecutarlo.

Además de los programas, hay un fichero más: "_defaults.json_". Este fichero simplemente indica los programas a usar por
defecto para cada extensión. O sea, el que se usará al hacer doble clic y el primero en el menú "Abrir con...".
