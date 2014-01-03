(define member
	(lambda
		(x xs)
		(if (= xs ())
			false
			(if (= x (head xs))
				true
				(member x (tail xs))
			)
		)
	)
)

(define seq
	(lambda (start end)
		(if (= start (+ 1 end))
			()
			(append (list start) (seq (+ start 1) end))
		)
	)
)
