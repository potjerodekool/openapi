package org.platonos.demo.restgen.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
class Person {

    @Id
    private var id: Int = 0;

    private var name: String? = null

}
