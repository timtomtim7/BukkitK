package blue.sparse.bukkitk.commands

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

interface Parser<in T, out R>
{
	@Suppress("UNCHECKED_CAST")
	companion object
	{
		private val registeredParsers: MutableMap<Class<*>, Parser<String, *>> = HashMap()
		private val pluginParsers: MutableMap<Plugin, MutableList<Parser<String, *>>> = HashMap()

		init
		{
			registeredParsers.put(Int::class.java, of(Integer::parseInt))
			registeredParsers.put(java.lang.Integer::class.java, of(Integer::parseInt))
			registeredParsers.put(Long::class.java, of(java.lang.Long::parseLong))
			registeredParsers.put(java.lang.Long::class.java, of(java.lang.Long::parseLong))
			registeredParsers.put(Float::class.java, of(java.lang.Float::parseFloat))
			registeredParsers.put(java.lang.Float::class.java, of(java.lang.Float::parseFloat))
			registeredParsers.put(Double::class.java, of(java.lang.Double::parseDouble))
			registeredParsers.put(java.lang.Double::class.java, of(java.lang.Double::parseDouble))

			registeredParsers.put(Player::class.java, of<String, Player>(Bukkit::getPlayer))
			registeredParsers.put(OfflinePlayer::class.java, of<String, OfflinePlayer>(Bukkit::getOfflinePlayer))
			registeredParsers.put(UUID::class.java, of(UUID::fromString))
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

		fun <T> register(plugin: Plugin, parser: Parser<String, T>, clazz: Class<T>)
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

		inline fun <reified T> register(plugin: JavaPlugin, parser: Parser<String, T>)
		{
			register(plugin, parser, T::class.java)
		}

		inline fun <reified T> get(): Parser<String, T>?
		{
			return get(T::class.java)
		}

		fun <T> get(clazz: Class<T>): Parser<String, T>?
		{
			println("Getting parser for class ${clazz.name}")
			return registeredParsers[clazz] as Parser<String, T>?
		}

		inline fun <T, R> of(crossinline body: (T) -> R?): Parser<T, R>
		{
			return object : Parser<T, R>
			{
				override fun invoke(value: T): R?
				{
					return body(value)
				}
			}
		}
	}

	operator fun invoke(value: T): R?

	infix fun <V> and(body: (R) -> V?): Parser<T, V>
	{
		return and(of(body))
	}

	infix fun <V> and(next: Parser<R, V>): Parser<T, V>
	{
		return of { next(this(it) ?: return@of null) }
	}

	infix fun onlyIf(body: (R) -> Boolean): Parser<T, R>
	{
		return of {
			val value = this(it) ?: return@of null
			return@of if (body(value)) value else null
		}
	}
}

inline fun <reified T> parser(): Parser<String, T>?
{
	return Parser.get(T::class.java)
}

inline fun <reified T> parser(body: Parser<String, T>.() -> Parser<String, T>): Parser<String, T>?
{
	val parser = Parser.get(T::class.java) ?: return null
	return body(parser)
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
