package blue.sparse.bukkitk.extensions

import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue
import org.bukkit.metadata.Metadatable
import org.bukkit.plugin.java.JavaPlugin

data class MetadataContext(private val metadatable: Metadatable, private val plugin: JavaPlugin)
{
	inline operator fun <reified T> get(key: String): T?
	{
		return getMetadataValu(key)?.value() as? T
	}

	fun getMetadataValu(key: String): MetadataValue?
	{
		return metadatable.getMetadata(key).find { it.owningPlugin == plugin }
	}

	operator fun set(key: String, value: Any)
	{
		metadatable.setMetadata(key, FixedMetadataValue(plugin, value))
	}
}

inline fun <T> Metadatable.metadata(plugin: JavaPlugin, body: (MetadataContext) -> T): T
{
	return body(MetadataContext(this, plugin))
}