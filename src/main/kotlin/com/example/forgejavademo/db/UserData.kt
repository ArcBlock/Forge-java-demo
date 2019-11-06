package com.example.forgejavademo.db

import org.springframework.context.annotation.Primary
import org.springframework.data.annotation.Id
import org.springframework.data.repository.CrudRepository
import java.io.Serializable
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.GeneratedValue

/**
 *
 *     █████╗ ██████╗  ██████╗██████╗ ██╗      ██████╗  ██████╗██╗  ██╗
 *    ██╔══██╗██╔══██╗██╔════╝██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝
 *    ███████║██████╔╝██║     ██████╔╝██║     ██║   ██║██║     █████╔╝
 *    ██╔══██║██╔══██╗██║     ██╔══██╗██║     ██║   ██║██║     ██╔═██╗
 *    ██║  ██║██║  ██║╚██████╗██████╔╝███████╗╚██████╔╝╚██████╗██║  ██╗
 *    ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝╚═════╝ ╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝
 * Author       : shan@arcblock.io
 * Time         : 2019-11-05
 * Edited By    :
 * Edited Time  :
 * Description  :
 **/
@Entity
class UserData(var name: String ,
               var mobile: String,
               var email: String,
               var did: String){
  @javax.persistence.Id @GeneratedValue
  var id: Long = 0
  constructor(): this("","","","")
}

interface UserReposity: CrudRepository<UserData, Long>{
  fun findByDid(did: String)
}