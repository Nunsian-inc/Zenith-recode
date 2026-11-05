-- Figura-compatible math helpers used by avatar scripts.
function math.clamp(value, minimum, maximum)
    if minimum > maximum then
        return maximum
    end
    return value > maximum and maximum or value < minimum and minimum or value
end

function math.lerp(a, b, delta)
    return a + (b - a) * delta
end

function math.round(value)
    return math.floor(value + 0.5)
end

function math.map(value, minimum1, maximum1, minimum2, maximum2)
    return (value - minimum1) / (maximum1 - minimum1) * (maximum2 - minimum2) + minimum2
end

function math.shortAngle(a, b)
    local angle = (a - b) % 360
    return angle - ((2 * angle) % 360)
end

function math.lerpAngle(a, b, delta)
    return (a + math.shortAngle(a, b) * delta) % 360
end

function math.sign(value)
    return value == 0 and 0 or value > 0 and 1 or -1
end

vec = vectors.vec
math.playerScale = 0.9375
math.worldScale = 1.0666666667
