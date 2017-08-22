package blue.sparse.bukkitk

import blue.sparse.bukkitk.commands.Parser
import blue.sparse.bukkitk.events.listen
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.SimpleCommandMap
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.SimplePluginManager
import org.bukkit.plugin.java.JavaPlugin

class BukkitKPlugin : JavaPlugin()
{
	companion object
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
	}

	override fun onEnable()
	{
		listen<PluginDisableEvent> {
			println("plugin disable")
			val commands = pluginCommandMap[it.plugin] ?: return@listen
			pluginCommandMap.remove(it.plugin)
			commands.forEach { it.unregister(commandMap) }
			actualCommandMap.values.removeAll(commands)

			println("Unregistered ${commands.size} commands for ${it.plugin.name}")

			Parser.unregisterAll(it.plugin)
		}
	}
}