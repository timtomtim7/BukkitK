package blue.sparse.bukkitk

import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue
import org.bukkit.metadata.Metadatable
import org.bukkit.plugin.java.JavaPlugin

data class MetadataContext(private val metadatable: Metadatable, private val plugin: JavaPlugin)
{
	operator fun get(key: String): MetadataValue?
	{
		return metadatable.getMetadata(key).find { it.owningPlugin == plugin }
	}

	operator fun set(key: String, value: Any)
	{
		metadatable.setMetadata(key, FixedMetadataValue(plugin, value))
	}
}

fun <T> Metadatable.metadata(plugin: JavaPlugin, body: MetadataContext.() -> T): T
{
	return body(MetadataContext(this, plugin))
}