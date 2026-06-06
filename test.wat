(module
  (type $t0 (func (param i32 i32) (result i32)))

  (func $add (type $t0)
    local.get 0
    local.get 1
    i32.add)

  (export "add" (func $add))
)