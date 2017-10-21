package blue.sparse.bukkitk.extensions

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin

fun JavaPlugin.delay(delay: Long, body: () -> Unit) = Bukkit.getScheduler().scheduleSyncDelayedTask(this, body, delay)
fun JavaPlugin.repeat(delay: Long, body: () -> Unit) = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, body, 0, delay)

val String.colored: String get() = ChatColor.translateAlternateColorCodes('&', this)
val String.coloured: String inline get() = colored

val Collection<String>.colored: List<String> get() = map(String::colored)
val Collection<String>.coloured: List<String> inline get() = colored

fun coloredListOf(vararg strings: String) = strings.map(String::colored)
fun colouredListOf(vararg strings: String) = coloredListOf(*strings)

//Static extensions, please.
//val <T: JavaPlugin> T.instance: T get() = JavaPlugin.getPlugin(this::class.java)