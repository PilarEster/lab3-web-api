package es.unizar.webeng.lab3

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
class EmployeeController(
    private val repository: EmployeeRepository
) {

    @GetMapping("/employees")
    fun all(): Iterable<Employee> = repository.findAll()

    /* Create new employee: devuelve 201 y header location que apunta a la cabecera del objeto creado */
    @PostMapping("/employees")
    fun newEmployee(@RequestBody newEmployee: Employee): ResponseEntity<Employee> {
        val employee = repository.save(newEmployee) /* Si el ID está vacío (null): la libreria genera un ID adecuado para el mismo */
        /* Nos guarda puerto, host y si trabajo con hhtp o https */
        val location = ServletUriComponentsBuilder
            .fromCurrentServletMapping()
            .path("/employees/{id}")
            .build(employee.id)
        /* Una vez creado el location, uso objeto response entity que permite añadir info adicional al objeto creado para devolverlo por http */
        return ResponseEntity.created(location).body(employee)
    }

    /* Respresenta la recuperación de un único empleado */
    @GetMapping("/employees/{id}")
    fun one(@PathVariable id: Long): Employee = repository.findById(id).orElseThrow { EmployeeNotFoundException(id) }
    /* findBy(id): dos posibles valores: o el id o nulo .
        Usamos la técnica de lanzar una excepción, que la he creado yo más abajo donde vemos que está anotada con un response status:
        Ventaja: hemos hecho un código m-as sencillo, usamos la excepcion como señal pa avisar a framework q está por debajo que tiene
        que responder a algo, que es una situacion anomala
    
    */

    /* Idea de put. usuario decide donde está la id, dos casos:
    -No existe id ni objeto por tanto y lo creamos (devuelvo un 201): tell location para que sea cual es el enlace directo al recurso creado
    -El id ya está y lo actualizo. (devuelvo un 200)
    Podemos avisar al usuario de en qué caso estamos
     */
    @PutMapping("/employees/{id}")
    fun replaceEmployee(@RequestBody newEmployee: Employee, @PathVariable id: Long): ResponseEntity<Employee> {
        val location = ServletUriComponentsBuilder
            .fromCurrentServletMapping()
            .path("/employees/{id}")
            .build(id)
            .toASCIIString()
            /* El objeto opcions tiene map: voy a convertir el contenido de option en otro option de otro tipo, si es que hay dato, sino paso de largo */
        val (status, body) = repository.findById(id)
            .map { employee ->
                employee.name = newEmployee.name
                employee.role = newEmployee.role
                repository.save(employee)
                HttpStatus.OK to employee
            }.orElseGet {   /* Situación de nuevo empleado: sobreescribimos, salvamos el empleado y devolvemos el empleado creado como respuesta a la función. */
                newEmployee.id = id
                repository.save(newEmployee)
                HttpStatus.CREATED to newEmployee
            } /* Status: si nuevo (created), sino (ok) */
            /* Ahora construimos respuesta en la que usamos metodo de estatus pq no sabemos cual es y así asignamos uno correcto */
        return ResponseEntity.status(status).header("Content-Location", location).body(body)
    }

    /* Borrado: la especificación de http no es clara, no te dice cómo funciona en caso de querer borrar una @ en la que no haya nada, 
        queda a interpretación de las guías y en este caso nos decantamos por devolver 404
     */
    @DeleteMapping("/employees/{id}")
    fun deleteEmployee(@PathVariable id: Long): ResponseEntity<Employee> = repository.findById(id).map {
        repository.deleteById(id)   /* buscamos si está el recurso y si está lo borramos */
        ResponseEntity.noContent().build<Employee>()    /* devuelve vaćío (?) */
    }.orElseThrow { EmployeeNotFoundException(id) }     /* Si no está, devuelve un 404 */
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class EmployeeNotFoundException(id: Long) : Exception("Could not find employee $id")
