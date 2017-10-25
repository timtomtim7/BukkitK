package blue.sparse.bukkitk.commands

import blue.sparse.bukkitk.extensions.colored
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class CommandContext(val sender: CommandSender, val command: Command, val label: String, val args: Array<out String>)
{
	fun reply(message: String)
	{
		sender.sendMessage(message)
	}

	fun reply(vararg messages: String)
	{
		sender.sendMessage(messages)
	}

	fun fail(message: String): Nothing = fail { reply("${ChatColor.RED}$message") }

	inline fun fail(body: () -> Unit): Nothing
	{
		body()
		throw FailCommandInterrupt()
	}

	fun failIf(value: Boolean, message: String) = failIf(value, { reply("${ChatColor.RED}$message") })

	inline fun failIf(value: Boolean, body: () -> Unit)
	{
		if (value) fail(body)
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
		} catch (e: CommandInterrupt) { }

		throw SubCommandInterrupt()
	}

	fun <T> optional(string: String?, parser: Parser<String, T>?): T?
	{
		return string?.let {
			try
			{
				parser?.invoke(it)
			} catch (e: Throwable) { null }
		}
	}

	fun <T> optional(index: Int, parser: Parser<String, T>?): T?
	{
		return optional(args.getOrNull(index), parser)
	}

	inline fun <reified T> optional(index: Int): T?
	{
		return optional(index, Parser.get(T::class.java))
	}

	fun <T> expect(string: String?, parser: Parser<String, T>?): T
	{
		return expect(string, parser) { reply("\u00a7c${command.usage}") }
	}

	inline fun <T> expect(string: String?, parser: Parser<String, T>?, fallback: () -> Unit): T
	{
		return optional(string, parser) ?: run {
			fallback()
			throw CommandInterrupt()
		}
	}

	fun <T> expect(index: Int, parser: Parser<String, T>?): T
	{
		return expect(index, parser) { reply("\u00a7c${command.usage}") }
	}

	inline fun <T> expect(index: Int, parser: Parser<String, T>?, fallback: () -> Unit): T
	{
		return expect(args.getOrNull(index), parser, fallback)
	}

	inline fun <reified T> expect(index: Int): T
	{
		return expect(index, Parser.get(T::class.java))
	}

	inline fun <reified T> expect(index: Int, crossinline fallback: () -> Unit): T
	{
		return expect(index, Parser.get(T::class.java), fallback)
	}

	open class CommandInterrupt: Throwable()
	class SubCommandInterrupt: CommandInterrupt()
	class FailCommandInterrupt: CommandInterrupt()
}

inline fun JavaPlugin.command(
		name: String,
		description: String = "A ${this.name} command",
		usage: String = "$name [args]",
		permission: String? = null,
		permissionMessage: String = "&cYou don't have permission for this.".colored,
		vararg aliases: String,
		argumentMode: ArgumentMode = ArgumentMode.NORMAL,
		crossinline body: CommandContext.() -> Unit)
{
	val command: Command = object : Command(name, description, usage, aliases.toList())
	{
		init
		{
			this.permission = permission
			this.permissionMessage = permissionMessage
		}

		override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean
		{
			val ctx = CommandContext(sender, this, label, argumentMode.converter(args))

			try {
				ctx.failIf(!sender.hasPermission(this.permission), this.permissionMessage)
				body(ctx)
			}
			catch (e: CommandContext.CommandInterrupt) { }

			return true
		}
	}
	CommandHandler.register(this, command)
}