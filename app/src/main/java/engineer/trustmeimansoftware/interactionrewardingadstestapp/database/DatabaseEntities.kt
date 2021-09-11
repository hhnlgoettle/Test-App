
package engineer.trustmeimansoftware.interactionrewardingadstestapp.database


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Currency constructor(
    @PrimaryKey
    val name: String,
    var value: Long,
)

