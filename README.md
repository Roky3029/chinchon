# CHINCHON - A SPANISH TRADITIONAL CARD GAME

### Historical overview

Whilst Chinchon is a game that is played not only in Spain, but also in some other Spanish-speaking countries like Argentina, Colombia, Mexico, etc.

Since this game is played in a lot of places among the globe and every one will add their own little tweaks to this game, as it is normal in a card game, this game has been programmed to follow the rules I use when playing with my family.

### Main objective of the game

The main objective of the game is tio not reach, or surpass, 101 points. You can achieve this through several ways, closing, scoring -10 points, which will effectively reduce your current score by 10 points, and so on. The game is finished when either the two players agree on finishing or one player achieves a straight of 7 seven cards of the same suit. That is called Chinchon, and effectively wins the game on the spot.

### How to play?

The first clarification to be done is that this game is played with 2 standard Spanish decks, where all the 8s and 9s have been removed (in this implementation, Jokers have also not been implemented), thus, leaving a deck of 1-7 and 10-12 of each suit (Espadas (Swords), Bastos (Clubs), Oros (Coins) and Copas (Glasses)) twice.

In Chinchon each player is dealt 7 cards from among the two reduced decks. In each of their turns, the player may:
1) Pick a card from the discard pile (it will be the card the other player has discarded. If it is the first turn, the top card of the library is selected as the first discard)
2) Get a random card from the library

Afterwards, since the player would have 8 cards after doing that, and the rules state that a player must have 7 cards in any way, they have to discard one (prefferably the one that makes the least synergy with the rest of their cards, for further info, see **End of the game**).

After discarding and right before yielding the turn to their opponent, in case the player can end the round, they may do it (See **End of the game**). Otherwise, it is the opponents turn.

### End of the game

To finish the round a player must have either of these 3 cases:

1) Two groups of 3 cards and the not-grouped card to have a value of 3 or less
2) A group of 4 cards and another one of 3 cards
3) A straight of 7 cards of the same suit

In either case, the groups can be made with a straight of cards of the same suit, or with cards of the same number (since there are two decks, a total of 8 cards of the same number can appear during the round).
Depending on the case, there will be a different outcome, respectively:

1) This is called "closing". When closing, the players reveal all the groups they have formed, and, in the case that any non-grouped cards could be added to a group (lets say you have a 7 of "bastos" and the other player has a group of 3 7s, you may add that card to that group and thus you would prevent adding 7 points to your total), you may do it. When no more cards can be added, the round finishes and each player adds to their counter the sum of the cards they have not been able to group.
2) This is called "minus 10". This has the same procedure as closing but with the difference that the player who did it substracts 10 points from their count (you may have a negative amount of points) and the other players cannot add any card whatsoever to any group. The cards they have not grouped are added.
3) This is called "Chinchon". If you score a "chinchon", you win automatically the whole game.

After scoring, another round is played until the players agree to finish playing or a player has a score of 102 points or more, which would result in they losing the game.

### About me

Hey! I'm Miguel, a Computer Engineering student, if you are considering contacting me, please write an email to `mjibarb30@gmail.com`, or, as an alternative, check my personal README on Github, where you'll find all the necessary information.

[Personal README](https://github.com/Roky3029/Roky3029)

> "Just play. Have fun. Enjoy the game."
> 
> > Michael Jordan
