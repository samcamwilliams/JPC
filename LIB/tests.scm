(define factorial
	(lambda (x)
		(if (= x 1)
			1
			(* x (factorial (- x 1)))
		)
	)
)

(define binding-check
	(lambda (x y)
		(+ x y)
	)
)
