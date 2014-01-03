; Mode transitions
(bind "\(" "(")
(bind "i" "(")
(bind "(.+)\)" "($1)")

; Player movements
(bind "([0-9]+)G" "(goto $1)" true)
(bind "([0-9]+)sk" "(seek $1)" true)
(bind "sk" "(seek $0)")

; Volume
(bind "([\+\-])([0-9]+)v" "(volume ($1 (volume) $2))" true)
(bind "([0-9]+)v" "(volume $1)" true)
(bind "v" "(volume $0)")

; Buffer control
(bind "<Alt>([0-9])" "(buffer-focus $1" true)
(bind "d" "(buffer-delete)" true)

; Load search in current buffer
(bind "r" "(artist '$0' (buffer-active))")
(bind "a" "(album '$0' (buffer-active))")
(bind "t" "(track '$0' (buffer-active))")

; Load search in new buffer
(bind "R" "(artist '$0')")
(bind "A" "(album '$0')")
(bind "T" "(track '$0')")

; Single character player controls
(bind "p" "(pause)" true)
(bind "P" "(play)" true)
(bind "S" "(stop)" true)
(bind "s" "(sync)" true)
(bind "n" "(next)" true)
(bind "l" "(prev)" true)
(bind "L" "(load)" true)
(bind "x" "(delete)" true)
(bind "X" "(clear)" true)
