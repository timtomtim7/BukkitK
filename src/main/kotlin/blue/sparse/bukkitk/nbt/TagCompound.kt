package blue.sparse.bukkitk.nbt

class TagCompound : MutableMap<String, Any>
{
	private val backingMap = HashMap<String, Any>()

	override val size: Int get() = backingMap.size
	override val entries: MutableSet<MutableMap.MutableEntry<String, Any>> get() = backingMap.entries
	override val keys: MutableSet<String> get() = backingMap.keys
	override val values: MutableCollection<Any> get() = backingMap.values

	override fun containsKey(key: String) = backingMap.containsKey(key)
	override fun containsValue(value: Any) = backingMap.containsValue(value)
	override fun get(key: String) = backingMap[key]
	override fun isEmpty() = backingMap.isEmpty()
	override fun clear() = backingMap.clear()
	override fun putAll(from: Map<out String, Any>) = from.forEach { k, v -> put(k, v) }
	override fun remove(key: String) = backingMap.remove(key)

	override fun put(key: String, value: Any): Any?
	{
		if (!isOfAcceptableType(value)) throw IllegalArgumentException("Unacceptable type for NBT")
		return backingMap.put(key, value)
	}

	fun isOfAcceptableType(obj: Any?): Boolean
	{
		if (obj == null) return true
		if (obj is Collection<*>)
		{
			if (obj.size == 0) return true
			if (obj.all { it == null }) return true
			val first = obj.firstOrNull { it != null } ?: return true
			return obj.all { it == null || it::class.java.isInstance(first) }
		}
		return obj is Number || obj is Boolean || obj is ByteArray || obj is IntArray || obj is TagCompound
	}

	inline fun <reified T> ifPresentAndCast(key: String, body: (T) -> Unit)
	{
		val value = get(key) ?: return
		if (value is T) body(value)
	}
}