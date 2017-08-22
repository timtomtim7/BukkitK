package blue.sparse.bukkitk

import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

interface Parser<in T, out R>
{
	@Suppress("UNCHECKED_CAST")
	companion object
	{
		private val registeredParsers: MutableMap<Class<*>, Parser<String, *>> = HashMap()
		private val pluginParsers: MutableMap<Plugin, MutableList<Parser<String, *>>> = HashMap()

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
			return registeredParsers[clazz] as Parser<String, T>?
		}

//		fun <T> register(clazz: Class<T>, parser: Parser<String, T>)

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

//	infix fun otherwise(body: () -> String)
//	{
//
//	}
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

val Int.Companion.parser: Parser<String, Int>
	get() = Parser.of {
		try
		{
			Integer.parseInt(it)
		} catch (e: NumberFormatException)
		{
			null
		}
	}

val Double.Companion.parser: Parser<String, Double>
	get() = Parser.of {
		try
		{
			java.lang.Double.parseDouble(it)
		} catch (e: NumberFormatException)
		{
			null
		}
	}