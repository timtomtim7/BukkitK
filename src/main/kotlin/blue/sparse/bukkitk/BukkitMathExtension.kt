package blue.sparse.bukkitk

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

operator fun Vector.plusAssign(other: Vector)
{
	this.add(other)
}

operator fun Vector.plusAssign(other: Block)
{
	this.add(other.vector)
}

operator fun Vector.plusAssign(other: Location)
{
	this.add(other.vector)
}

operator fun Vector.plusAssign(other: Number)
{
	assign(x + other.toDouble(), y + other.toDouble(), z + other.toDouble())
}

operator fun Vector.minusAssign(other: Vector)
{
	this.subtract(other)
}

operator fun Vector.minusAssign(other: Block)
{
	this.subtract(other.vector)
}

operator fun Vector.minusAssign(other: Location)
{
	this.subtract(other.vector)
}

operator fun Vector.minusAssign(other: Number)
{
	assign(x - other.toDouble(), y - other.toDouble(), z - other.toDouble())
}

operator fun Vector.timesAssign(other: Vector)
{
	this.multiply(other)
}

operator fun Vector.timesAssign(other: Block)
{
	this.multiply(other.vector)
}

operator fun Vector.timesAssign(other: Location)
{
	this.multiply(other.vector)
}

operator fun Vector.timesAssign(other: Number)
{
	assign(x * other.toDouble(), y * other.toDouble(), z * other.toDouble())
}

operator fun Vector.divAssign(other: Vector)
{
	this.divide(other)
}

operator fun Vector.divAssign(other: Block)
{
	this.divide(other.vector)
}

operator fun Vector.divAssign(other: Location)
{
	this.divide(other.vector)
}

operator fun Vector.divAssign(other: Number)
{
	assign(x / other.toDouble(), y / other.toDouble(), z / other.toDouble())
}

operator fun Vector.remAssign(other: Vector)
{
	this.assign(x % other.x, y % other.y, z % other.z)
}

operator fun Vector.remAssign(other: Block)
{
	this.assign(x % other.x, y % other.y, z % other.z)
}

operator fun Vector.remAssign(other: Location)
{
	this.assign(x % other.x, y % other.y, z % other.z)
}

operator fun Vector.remAssign(other: Number)
{
	assign(x % other.toDouble(), y % other.toDouble(), z % other.toDouble())
}

operator fun Vector.plus(other: Vector): Vector = this.clone().add(other)
operator fun Vector.minus(other: Vector): Vector = this.clone().subtract(other)
operator fun Vector.times(other: Vector): Vector = this.clone().multiply(other)
operator fun Vector.div(other: Vector): Vector = this.clone().divide(other)
operator fun Vector.rem(other: Vector): Vector = this.clone().assign(x % other.x, y % other.y, z % other.z)

operator fun Vector.plus(other: Block): Vector = other.vector.add(this)
operator fun Vector.minus(other: Block): Vector = other.vector.subtract(this)
operator fun Vector.times(other: Block): Vector = other.vector.multiply(this)
operator fun Vector.div(other: Block): Vector = other.vector.divide(this)
operator fun Vector.rem(other: Block): Vector = other.vector.assign(x % other.x, y % other.y, z % other.z)

operator fun Vector.plus(other: Location): Vector = other.vector.add(this)
operator fun Vector.minus(other: Location): Vector = other.vector.subtract(this)
operator fun Vector.times(other: Location): Vector = other.vector.multiply(this)
operator fun Vector.div(other: Location): Vector = other.vector.divide(this)
operator fun Vector.rem(other: Location): Vector = other.vector.assign(x % other.x, y % other.y, z % other.z)

operator fun Vector.plus(other: Number): Vector = this.clone().assign(x + other.toDouble(), y + other.toDouble(), z + other.toDouble())
operator fun Vector.minus(other: Number): Vector = this.clone().assign(x - other.toDouble(), y - other.toDouble(), z - other.toDouble())
operator fun Vector.times(other: Number): Vector = this.clone().assign(x * other.toDouble(), y * other.toDouble(), z * other.toDouble())
operator fun Vector.div(other: Number): Vector = this.clone().assign(x / other.toDouble(), y / other.toDouble(), z / other.toDouble())
operator fun Vector.rem(other: Number): Vector = this.clone().assign(x % other.toDouble(), y % other.toDouble(), z % other.toDouble())

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

