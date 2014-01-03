(define get
	(lambda
		(el dict)
		(if (= () dict)
			undefined
			(if
				(= el (element 1 (head dict)))
				(element 2 (head dict))
				(get el (tail dict))
			)
		)
	)
)

(define set
	(lambda
		(el val dict)
		(if (= () dict)
			(el val)
			(if
				(= el (element 1 (head dict)))
				(append (list (list el val)) dict)
				(append
					((head dict))
					(set el val (tail dict))
				)
			)
		)
	)
)
