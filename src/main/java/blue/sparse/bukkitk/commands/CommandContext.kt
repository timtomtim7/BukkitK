package blue.sparse.bukkitk.commands

import blue.sparse.bukkitk.BukkitKPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

data class CommandContext(val sender: CommandSender, val command: Command, val label: String, val args: Array<out String>)
{
	override fun equals(other: Any?): Boolean
	{
		if (this === other) return true
		if (other !is CommandContext) return false

		if (sender != other.sender) return false
		if (command != other.command) return false
		if (label != other.label) return false
		if (!Arrays.equals(args, other.args)) return false

		return true
	}

	override fun hashCode(): Int
	{
		var result = sender.hashCode()
		result = 31 * result + command.hashCode()
		result = 31 * result + label.hashCode()
		result = 31 * result + Arrays.hashCode(args)
		return result
	}

	fun reply(message: String)
	{
		sender.sendMessage(message)
	}

	inline fun subcommand(name: String, body: CommandContext.() -> Unit)
	{
		if (args.isEmpty()) return
		if (args[0] == name)
			runSubCommand(body)
	}

	inline fun subcommand(regex: Regex, body: CommandContext.() -> Unit)
	{
		if (args.isEmpty()) return
		if (args[0].matches(regex))
			runSubCommand(body)
	}

	inline fun runSubCommand(body: CommandContext.() -> Unit)
	{
		val newArgs = Array(args.size - 1, { args[it + 1] })

		try
		{
			body(CommandContext(sender, command, label, newArgs))
		} catch (e: CommandInterrupt)
		{
		}
		throw SubCommandInterrupt()
	}

	fun <T> expect(string: String?, parser: Parser<String, T>?, fallback: () -> Unit = { sender.sendMessage("Invalid syntax") }): T
	{
		return string?.let {
			try
			{
				parser?.invoke(it)
			} catch (e: Throwable)
			{
				null
			}
		} ?: run {
			fallback()
			throw CommandInterrupt()
		}

//		if(parser != null)
//		{
//			val result = if (string == null) null else parser(string)
//			if (result != null) return result
//		}
//
//		fallback()
//		throw CommandInterrupt()
	}

	fun <T> expect(index: Int, parser: Parser<String, T>?, fallback: () -> Unit = { sender.sendMessage("Invalid syntax") }): T
	{
		return expect(args.getOrNull(index), parser, fallback)
	}

	inline fun <reified T> expect(index: Int): T
	{
		return expect(index, Parser.get(T::class.java))
	}

	inline fun <reified T> expect(index: Int, noinline fallback: () -> Unit): T
	{
		return expect(index, Parser.get(T::class.java), fallback)
	}

	open class CommandInterrupt : Throwable()
	class SubCommandInterrupt : CommandInterrupt()
}

inline fun JavaPlugin.command(name: String, description: String = "", usage: String = "$name [args]", vararg aliases: String, useQuotes: Boolean = false, crossinline body: CommandContext.() -> Unit)
{
	val command: Command = object : Command(name, description, usage, aliases.toList())
	{
		override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean
		{
			val realArgs = if (useQuotes) parseQuotes(args) else args
			try
			{
				body(CommandContext(sender, this, label, realArgs))
			} catch (e: CommandContext.CommandInterrupt)
			{
			}

			return true
		}
	}
	BukkitKPlugin.register(this, command)
}