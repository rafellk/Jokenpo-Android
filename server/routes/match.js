var express = require('express');
var router = express.Router();

var Match = require('../models/match');

/**
 * Retrieves the match that is mapped by the specified id
 */
router.get('/id/:id', function (req, res, next) {
    let id = req.params.id;

    Match.findById(id, (error, match) => {
        if (error) {
            res.status(500).json({
                error: error
            });
            return;
        }

        res.json(match);
    });
});

/**
 * Retrieves the match that a specific player is playing
 */
router.get('/player/playing/:playerId', function (req, res, next) {
    let playerId = req.params.playerId;

    Match.find({ $and: [
        { $or:[ { player1: playerId }, { player2: playerId } ] },
        { playing: true }
    ]}, (error, match) => {
        if (error) {
            res.status(500).json({
                error: error
            });
            return;
        }

        res.json(match);
    });
});

/**
 * Retrieves the matchs that a specific player already played
 */
router.get('/player/history/:playerId', function (req, res, next) {
    let playerId = req.params.playerId;

    Match.find({ $and: [
        { $or:[ { player1: playerId }, { player2: playerId } ] },
        { playing: false }
    ]}, (error, matches) => {
        if (error) {
            res.status(500).json({
                error: error
            });
            return;
        }

        res.json(matches);
    });
});

/**
 * Creates the match with the player 1, player 2 and playing boolean set to FALSE. The playing boolean will be set to TRUE when the player 2 accept the match
 */
router.post('/', function (req, res, next) {
    var match = new Match(req.body);
    match.player1_move = 'NONE';
    match.player2_move = 'NONE';
    
    match.save((error, match) => {
        if (error) {
            res.status(500).json(error);
            return;
        }

        // TODO: fire gcm service to the player 2 sending the match id

        res.status(201).json(match);
    });
});

/**
 * Updates de playing boolean property to TRUE and notifies the player 1 that the match can begin
 */
router.put('/accept/:id', function (req, res, next) {
    let id = req.params.id;

    Match.findOneAndUpdate({ _id: id }, { playing: true }, { upsert: false }, (error, match) => {
        if (error) {
            res.status(500).json(error);
            return;
        }

        // TODO: fire gcm service to the player 1 accepting the request

        res.sendStatus(200);
    });
});

/**
 * Removes the match that the player 2 declined and notifies the player 1 that the match was cancelled
 */
router.delete('/decline/:id', function (req, res, next) {
    let id = req.params.id;

    Match.delete({ _id: id }, (error) => {
        if (error) {
            res.status(500).json(error);
            return;
        }

        // TODO: fire gcm service to the player 1 declining the request

        res.sendStatus(200);
    });
});

/**
 * Updates the player move depending on the player that made the request. If the player that made the request was the last one then calculate the game result and determine which player is the winner and noties him
 */
router.put('/move/:matchId/:playerId/:move', function (req, res, next) {
    let matchId = req.params.matchId;
    let playerId = req.params.playerId;
    let move = req.params.move;

    Match.findById(matchId, (error, match) => {
        if (error) {
            res.status(500).json(error);
            return;
        }

        if (match.player1 == playerId) {
            match.player1_move = move;
         } else if (match.player2 == playerId) {
            match.player2_move = move;
        }

        // if both moves were set, then calculate the game result and update the winner property
        if (match.player1_move != 'NONE' && match.player2_move != 'NONE') {
            switch(match.player1_move) {
                case 'PAPER':
                switch(match.player2_move) {
                    case 'PAPER':
                    break;
                    case 'ROCK':
                    match.winner = match.player1;
                    break;
                    case 'SCISSORS':
                    match.winner = match.player2;
                    break;
                }
                break;
                case 'ROCK':
                switch(match.player2_move) {
                    case 'PAPER':
                    match.winner = match.player2;
                    break;
                    case 'ROCK':
                    break;
                    case 'SCISSORS':
                    match.winner = match.player1;
                    break;
                }
                break;
                case 'SCISSORS':
                switch(match.player2_move) {
                    case 'PAPER':
                    match.winner = match.player1;
                    break;
                    case 'ROCK':
                    match.winner = match.player1;
                    break;
                    case 'SCISSORS':
                    break;
                }
                break;
            }

            match.playing = false;
        }

        match.save((error, match) => {
            if (error) {
                res.status(500).json(error);
                return;
            }

            if (!match.playing) {
                // TODO: fire gcm service to both players informing the winner
            }

            res.sendStatus(200);
        });
    });
});

/**
 * Updates the specified match with the winner by rage quit. The user that makes this request is the rage quitter and will loose the match consequently
 */
router.put('/ragequit/:matchId/:playerId', function (req, res, next) {
    let matchId = req.params.matchId;
    let playerId = req.params.playerId;

    Match.findById(matchId, (error, match) => {
        if (error) {
            res.status(500).json(error);
            return;
        }

        match.winner = (match.player1 == playerId) ? match.player2 : match.player1;

        match.save((error, match) => {
            if (error) {
                res.status(500).json(error);
                return;
            }

            // TODO: fire gcm service to both players informing the winner 

            res.status(200).json(match);
        });
    });
});

module.exports = router;