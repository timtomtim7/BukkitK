package blue.sparse.bukkitk.extensions

import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.util.Vector

val Block.vector: Vector get() = Vector(x, y, z)
val Location.vector: Vector get() = Vector(x, y, z)

fun Vector.assign(x: Number, y: Number, z: Number): Vector
{
	setX(x.toDouble())
	setY(y.toDouble())
	setZ(z.toDouble())
	return this
}

operator fun Vector.plusAssign(other: Vector) {	add(other) }
operator fun Vector.minusAssign(other: Vector) { subtract(other) }
operator fun Vector.timesAssign(other: Vector) { multiply(other) }
operator fun Vector.divAssign(other: Vector) { divide(other) }
operator fun Vector.remAssign(other: Vector) { assign(x % other.x, y % other.y, z % other.z) }

operator fun Vector.plusAssign(other: Block) { add(other.vector) }
operator fun Vector.minusAssign(other: Block) { subtract(other.vector) }
operator fun Vector.timesAssign(other: Block) { multiply(other.vector) }
operator fun Vector.divAssign(other: Block) { divide(other.vector) }
operator fun Vector.remAssign(other: Block) { this.assign(x % other.x, y % other.y, z % other.z) }

operator fun Vector.plusAssign(other: Location) { add(other.vector) }
operator fun Vector.minusAssign(other: Location) { subtract(other.vector) }
operator fun Vector.timesAssign(other: Location) { multiply(other.vector) }
operator fun Vector.divAssign(other: Location) { divide(other.vector) }
operator fun Vector.remAssign(other: Location) { this.assign(x % other.x, y % other.y, z % other.z) }

operator fun Vector.plusAssign(other: Number) { other.toDouble().let { assign(x + it, y + it, z + it) } }
operator fun Vector.minusAssign(other: Number) { other.toDouble().let { assign(x - it, y - it, z - it) } }
operator fun Vector.timesAssign(other: Number) { other.toDouble().let { assign(x * it, y * it, z * it) } }
operator fun Vector.divAssign(other: Number) { other.toDouble().let { assign(x / it, y / it, z / it) } }
operator fun Vector.remAssign(other: Number) { other.toDouble().let { assign(x % it, y % it, z % it) } }

operator fun Vector.plus(other: Vector): Vector = clone().add(other)
operator fun Vector.minus(other: Vector): Vector = clone().subtract(other)
operator fun Vector.times(other: Vector): Vector = clone().multiply(other)
operator fun Vector.div(other: Vector): Vector = clone().divide(other)
operator fun Vector.rem(other: Vector): Vector = Vector(x % other.x, y % other.y, z % other.z)

operator fun Vector.plus(other: Block): Vector = other.vector.add(this)
operator fun Vector.minus(other: Block): Vector = other.vector.subtract(this)
operator fun Vector.times(other: Block): Vector = other.vector.multiply(this)
operator fun Vector.div(other: Block): Vector = other.vector.divide(this)
operator fun Vector.rem(other: Block): Vector = Vector(x % other.x, y % other.y, z % other.z)

operator fun Vector.plus(other: Location): Vector = other.vector.add(this)
operator fun Vector.minus(other: Location): Vector = other.vector.subtract(this)
operator fun Vector.times(other: Location): Vector = other.vector.multiply(this)
operator fun Vector.div(other: Location): Vector = other.vector.divide(this)
operator fun Vector.rem(other: Location): Vector = Vector(x % other.x, y % other.y, z % other.z)

operator fun Vector.plus(other: Number): Vector = other.toDouble().let { Vector(x + it, y + it, z + it) }
operator fun Vector.minus(other: Number): Vector = other.toDouble().let { Vector(x - it, y - it, z - it) }
operator fun Vector.times(other: Number): Vector = other.toDouble().let { Vector(x * it, y * it, z * it) }
operator fun Vector.div(other: Number): Vector = other.toDouble().let { Vector(x / it, y / it, z / it) }
operator fun Vector.rem(other: Number): Vector = other.toDouble().let { Vector(x % it, y % it, z % it) }

fun Location.assign(x: Number, y: Number, z: Number): Location
{
	setX(x.toDouble())
	setY(y.toDouble())
	setZ(z.toDouble())
	return this
}



fun <T> Block.checkSameWorld(other: Block, body: () -> T): T
{
	if (world != other.world)
		throw IllegalArgumentException("Operations between blocks must be in the same world")
	return body()
}

operator fun Block.plus(other: Block): Block = checkSameWorld(other) { getRelative(other.x, other.y, other.z) }
operator fun Block.minus(other: Block): Block = checkSameWorld(other) { getRelative(-other.x, -other.y, -other.z) }
operator fun Block.times(other: Block): Block = checkSameWorld(other) { world.getBlockAt(x * other.x, y * other.y, z * other.z) }
operator fun Block.div(other: Block): Block = checkSameWorld(other) { world.getBlockAt(x / other.x, y / other.y, z / other.z) }
operator fun Block.rem(other: Block): Block = checkSameWorld(other) { world.getBlockAt(x % other.x, y % other.y, z % other.z) }

operator fun Block.plus(other: Int): Block = getRelative(other, other, other)
operator fun Block.minus(other: Int): Block = getRelative(-other, -other, -other)
operator fun Block.times(other: Int): Block = world.getBlockAt(x * other, y * other, z * other)
operator fun Block.div(other: Int): Block = world.getBlockAt(x / other, y / other, z / other)
operator fun Block.rem(other: Int): Block = world.getBlockAt(x % other, y % other, z % other)

operator fun Block.get(x: Int, y: Int, z: Int): Block = getRelative(x, y, z)