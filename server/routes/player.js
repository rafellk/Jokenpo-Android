var express = require('express');
var router = express.Router();

var Player = require('../models/player');

/**
 * Retrieves the player that is mapped by the specified id
 */
router.get('/id/:id', function (req, res, next) {
    let id = req.params.id;

    Player.findOne({_id: id}, (error, player) => {
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
    Player.find({ logged: true }, (error, players) => {
        if (error) {
            res.status(500).json({
                error: error
            });
            return;
        }

        res.json(players);
    });
});

/**
 * Creates the player and set the logged boolean flag to true automatically
 */
router.post('/signin', function (req, res, next) {
    req.body.logged = true;
    
    Player.findOneAndUpdate({ name: req.body.name }, req.body, { upsert: true, new: true }, (error, player) => {
        if (error) {
            res.status(500).json(error);
            console.log(error);
            return;
        }

        console.log(player);

        res.status(201).json(player);
    });
});

/**
 * Logs out the specified user
 */
router.put('/logout/:id', function (req, res, next) {
    let id = req.params.id;

    Player.findOneAndUpdate({ _id: id }, { logged: false }, { upsert: false }, (error, player) => {
        if (error) {
            res.status(500).json(error);
            return;
        }

        res.sendStatus(200);
    });
});

module.exports = router;