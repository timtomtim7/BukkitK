package blue.sparse.bukkitk.commands

import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

interface Parser<out T>
{
	companion object
	{
		private val registeredParsers: MutableMap<Class<*>, Parser<*>> = HashMap()
		private val pluginParsers: MutableMap<Plugin, MutableList<Parser<*>>> = HashMap()

		init
		{
			registeredParsers.put(Int::class.java, of(String::toInt))
			registeredParsers.put(Long::class.java, of(String::toLong))
			registeredParsers.put(Float::class.java, of(String::toFloat))
			registeredParsers.put(Double::class.java, of(String::toDouble))
			registeredParsers.put(java.lang.Integer::class.java, of(String::toInt))
			registeredParsers.put(java.lang.Long::class.java, of(String::toLong))
			registeredParsers.put(java.lang.Float::class.java, of(String::toFloat))
			registeredParsers.put(java.lang.Double::class.java, of(String::toDouble))

			registeredParsers.put(String::class.java, of { it })

			registeredParsers.put(Player::class.java, of(Bukkit::getPlayer))
			registeredParsers.put(OfflinePlayer::class.java, of(Bukkit::getOfflinePlayer))
			registeredParsers.put(UUID::class.java, of(UUID::fromString))
			registeredParsers.put(World::class.java, of(Bukkit::getWorld))
		}

		fun unregisterAll(plugin: Plugin)
		{
			val list = pluginParsers[plugin]
			if (list == null || list.isEmpty())
			{
				pluginParsers.remove(plugin)
				return
			}

			registeredParsers.values.removeAll(list)
		}

		fun <T> register(plugin: Plugin, parser: Parser<T>, clazz: Class<T>)
		{
			registeredParsers.put(clazz, parser)
			var list = pluginParsers[plugin]
			if (list == null)
			{
				list = ArrayList()
				pluginParsers[plugin] = list
			}
			list.add(parser)
		}

		inline fun <reified T> register(plugin: JavaPlugin, parser: Parser<T>)
		{
			register(plugin, parser, T::class.java)
		}

		inline fun <reified T> register(plugin: JavaPlugin, crossinline body: (String) -> T?)
		{
			register(plugin, of(body), T::class.java)
		}

		inline fun <reified T> get(): Parser<T>?
		{
			return get(T::class.java)
		}

		@Suppress("UNCHECKED_CAST")
		fun <T> get(clazz: Class<T>): Parser<T>?
		{
			return registeredParsers[clazz] as Parser<T>?
		}

		inline fun <R> of(crossinline body: (String) -> R?): Parser<R>
		{
			return object : Parser<R>
			{
				override fun invoke(value: String): R?
				{
					return body(value)
				}
			}
		}
	}

	operator fun invoke(value: String): T?

	infix fun onlyIf(body: (T) -> Boolean): Parser<T>
	{
		return of {
			val value = this(it) ?: return@of null
			return@of if (body(value)) value else null
		}
	}
}

fun parseQuotes(args: Array<out String>) = parseQuotes(args.joinToString(" "))

fun parseQuotes(message: String): Array<out String>
{
	val quoteRegex = Regex("\"([^\"]+)\"|([^\" ]+)")

	return quoteRegex.findAll(message).map {
		val spaced = it.groupValues.last()
		if (spaced.isEmpty())
			return@map it.groupValues[it.groupValues.size - 2]
		return@map spaced
	}.toList().toTypedArray()
}

enum class ArgumentMode(val converter: (Array<out String>) -> Array<out String>)
{
	NORMAL({ it }),
	QUOTES(::parseQuotes),
	COMMA_SEPARATED({ it.joinToString(" ").split(Regex(", ?")).toTypedArray() });
}