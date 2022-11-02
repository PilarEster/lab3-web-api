package es.unizar.webeng.lab3

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository : CrudRepository<Employee, Long> /* tipo de objeto y tipo de objeto de la clave primaria */
