; Player functions

(define play (lambda () (mpd @playing true)))
(define stop (lambda () (mpd @playing false)))
(define pause (lambda () (mpd @pause)))
(define next (lambda () (mpd @next)))
(define prev (lambda () (mpd @previous)))

(define seek (lambda (x) (mpd @track-pos x)))
(define goto (lambda (x) (mpd @playlist-pos x)))
(define volume
	(lambda x
		(if (= (length x) 1)
			(mpd @volume (head x))
			(mpd @volume)
		)
	)
)


; Playlist functions
(define delete
	(lambda x
		(if (= (length x) 0)
			(buffer-playlist-delete (mpd @playlist-pos))
			(eval (append (list buffer-playlist-delete) x))
		)
	)
)
(define add buffer-playlist-add)
(define clear buffer-playlist-clear)

(define load
	(lambda args
		(mpd playlist
			(if (= (length args) 1)
				(buffer-playlist (element 1 args))
				(buffer-playlist)
			)
		)
	)
)

(define sync
	(lambda args
		(eval
			(append
				(list
					(if (eval (append (list buffer-is-sync) args)) buffer-unsync buffer-sync)
				)
				args
			)
		)
	)
)

(define artist (lambda l (eval (append (list search-db @artist) l))))
(define album (lambda l (eval (append (list search-db @album) l))))
(define track (lambda l (eval (append (list search-db @track) l))))

(define search-db
	(lambda args
		(begin
			(if (= (length args) 3)
				(buffer (mpd @search (element 1 args) (element 2 args)) (element 3 args))
				(buffer (mpd @search (element 1 args) (element 2 args)))
			)
			(if (= (length args) 3)
				(buffer-name (element 2 args) (element 3 args))
				(buffer-name (element 2 args) (- (buffer-count) 1))
			)
		)
	)
)

(define bdelete buffer-delete)
