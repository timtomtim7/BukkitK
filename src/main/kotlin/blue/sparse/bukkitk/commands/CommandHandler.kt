package blue.sparse.bukkitk.commands

import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.SimpleCommandMap
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.EventPriority
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.generator.ChunkGenerator
import org.bukkit.plugin.*
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.InputStream
import java.util.logging.Logger

object CommandHandler
{
	private val commandMap: SimpleCommandMap
	private val actualCommandMap: MutableMap<String, Command>
	private val pluginCommandMap: MutableMap<JavaPlugin, MutableSet<Command>> = HashMap()

	init
	{
		val cmdMapField = SimplePluginManager::class.java.getDeclaredField("commandMap")
		cmdMapField.isAccessible = true
		commandMap = cmdMapField.get(Bukkit.getPluginManager()) as SimpleCommandMap
		val knownCommandsField = commandMap.javaClass.getDeclaredField("knownCommands")
		knownCommandsField.isAccessible = true

		@Suppress("UNCHECKED_CAST")
		actualCommandMap = knownCommandsField.get(commandMap) as MutableMap<String, Command>

		PluginDisableEvent.getHandlerList().register(RegisteredListener(null, EventExecutor { _, event ->
			if (event !is PluginDisableEvent) return@EventExecutor
			val plugin = event.plugin

			val commands = pluginCommandMap[plugin] ?: return@EventExecutor
			pluginCommandMap.remove(plugin)
			commands.forEach { it.unregister(commandMap) }
			actualCommandMap.values.removeAll(commands)

			println("Unregistered ${commands.size} commands for ${plugin.name}")

			Parser.unregisterAll(plugin)
		}, EventPriority.NORMAL, DummyPlugin(), true))
	}

	fun register(plugin: JavaPlugin, command: Command): Boolean
	{
		val register = commandMap.register(plugin.name.toLowerCase(), command)
		if (!register)
			return false

		val list = pluginCommandMap[plugin] ?: HashSet()
		list.add(command)
		pluginCommandMap[plugin] = list

		return true
	}

	class DummyPlugin : Plugin
	{
		override fun getDataFolder(): File = TODO("not implemented")
		override fun onCommand(p0: CommandSender?, p1: Command?, p2: String?, p3: Array<out String>?): Boolean = TODO("not implemented")
		override fun saveDefaultConfig() = TODO("not implemented")
		override fun getResource(p0: String?): InputStream = TODO("not implemented")
		override fun getName(): String = "Dummy"
		override fun onTabComplete(p0: CommandSender?, p1: Command?, p2: String?, p3: Array<out String>?): MutableList<String> = TODO("not implemented")
		override fun isNaggable(): Boolean = TODO("not implemented")
		override fun getLogger(): Logger = TODO("not implemented")
		override fun reloadConfig() = TODO("not implemented")
		override fun onEnable() = TODO("not implemented")
		override fun isEnabled(): Boolean = true
		override fun onLoad() = TODO("not implemented")
		override fun setNaggable(p0: Boolean) = TODO("not implemented")
		override fun getConfig(): FileConfiguration = TODO("not implemented")
		override fun getPluginLoader(): PluginLoader = TODO("not implemented")
		override fun getDescription(): PluginDescriptionFile = TODO("not implemented")
		override fun getServer(): Server = TODO("not implemented")
		override fun onDisable() = TODO("not implemented")
		override fun getDefaultWorldGenerator(p0: String?, p1: String?): ChunkGenerator = TODO("not implemented")
		override fun saveConfig() = TODO("not implemented")
		override fun saveResource(p0: String?, p1: Boolean) = TODO("not implemented")
	}
}