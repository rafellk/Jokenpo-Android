var express = require('express');
var router = express.Router();

var Player = require('../models/player');

/**
 * Retrieves the player that is mapped by the specified id
 */
router.get('/id/:id', function (req, res, next) {
    let id = req.params.id;

    Player.findById(id, (error, player) => {
        if (error) {
            res.status(500).json({
                error: error
            });
            return;
        }

        res.json(player);
    });
});

/**
 * Retrieves the players that are logged in the server
 */
router.get('/room', function (req, res, next) {
    console.log("entered here");
    Player.find({ logged: true }, (error, players) => {
        console.log("something happened here");
        if (error) {
            res.status(500).json({
                error: error
            });
            return;
        }
        console.log("returning the players");

        res.json(players);
    });
});

/**
 * Creates the player and set the logged boolean flag to true automatically
 */
router.post('/signin', function (req, res, next) {
    Player.findOneAndUpdate({ name: req.body.name }, req.body, { upsert: true }, (error, player) => {
        if (error) {
            res.status(500).json(error);
            return;
        }

        res.status(201).json(player);
    });
});

/**
 * Log out the specified user
 */
router.put('/logout/:id', function (req, res, next) {
    let id = req.params.id;

    Player.findOneAndUpdate({ _id: id }, req.body, { upsert: false }, (error, player) => {
        if (error) {
            res.status(500).json(error);
            return;
        }

        res.sendStatus(200);
    });
});

module.exports = router;