import sys
class Othello:
    DEFAULT_BOARD = "---------------------------xo------ox---------------------------"
    DEFAULT_PLAY = "x"
    DEFAULT_DEPTH = 2
    MOVES = "xo"
    #DIRECTIONS = [-1, 0, 1]
    NEIGHBORS = {(x, y) for x in  [-1, 0, 1] for y in  [-1, 0, 1]} - {(0, 0)}
    @staticmethod
    def boardifyString(board):
        """ Boardifies the given string. """
        b = []
        for i in range(8):
            i = 7 - i
            b.append(board[i*8:(i+1)*8])   
        return b
    @staticmethod
    def stringifyBoard(board):
        """ Stringifies the given board. """
        return "".join([item for sublist in board[::-1] for item in sublist])    
    def __init__(self, board=DEFAULT_BOARD, play=DEFAULT_PLAY, depth=DEFAULT_DEPTH, orig=True, parentCoord=None, origDepth=True):
        if orig:
            depth *= 2
            self.origDepth = depth
        else:
            self.origDepth = origDepth
        self.origBoard = []
        self.moves = []
        self.parentCoord = parentCoord
        self.board = []
        self.play = play
        self.prodigalSon = None
        self.depth = depth
        self.other = self.MOVES.replace(self.play, "")
        self.xCount = board.count("x")
        self.oCount = board.count("o")
        self.scores = {"x": self.xCount, "o": self.oCount}
        self.children = []
        self.score = {"x": 0, "o": 0}
        self.bestMove = {"coord":None, "scores":self.scores}
        for i in range(8):
            i = 7 - i
            self.board.append(list(board[i*8:(i+1)*8]))
        self.origBoard = [row[:] for row in self.board]
        
        self.getBestMove()
        
        
    def printTree(self):
        """ Recursively print a tree of all possible games to given depth. """
        self.printBoard(showDepth=True)
        for child in self.children:
            child.printTree()
    def printBoard(self, showDepth=False):
        """ Prints the board. """
        for row in self.board:
            print ("--->" * (self.origDepth - self.depth) * showDepth) + " " + " ".join(row)
        print "\n"
    
    def getBestMove(self):
        """ Finds the best move by recursive generation of children. """
        self.children = []
        if self.depth > 0:
            for coord in self.getPossiblePlays():
                #self.origBoard = [row[:] for row in self.board]
                self.playMove(coord)
                self.children.append(Othello(board=self.stringifyBoard([row[:] for row in self.board]), play=self.other, depth=self.depth-1, orig=False, parentCoord=coord, origDepth=self.origDepth))
                self.board = [row[:] for row in self.origBoard]
            #bm = max(self.children, key = k.)
            if self.depth > 1:
                if self.children:
                    bm = max(self.children, key=lambda k: k.bestMove["scores"][self.play])
                    self.bestMove = {"coord":bm.parentCoord, "scores":bm.bestMove["scores"]}
                #self.scores = bestMove
            else:
                if self.children:
                    bm = max(self.children, key=lambda k: k.scores[self.play])
                    self.bestMove = {"coord":bm.parentCoord, "scores":bm.scores}
            #self.bestMove = {"score": bm}
        
    def getNeighbors(self, coord, checkOccupied=False):
        """ Returns the neighbors of a spot without wrapping. If checkOccupied, doesn't return occupied spots. """
        x, y = coord
        neighbors = set()#NEIGHBORS.copy()
        for c in self.NEIGHBORS:
            x1, y1 = c[0] + x, c[1] + y
            if 0 <= x1 < 8 and 0 <= y1 < 8:
                neighbors.add((x1, y1))
        
        if checkOccupied:
            noVacancies = set()
            for coord in neighbors:
                if self.board[coord[1]][coord[0]] != "-":
                    noVacancies.add(coord)
            return neighbors - noVacancies
        return neighbors
       
                
    def getPossiblePlays(self):
        """ Returns the possible move coordinates. """
        possibleMoves = set()
        for y in xrange(8):
            for x in xrange(8):
                if self.board[y][x] == self.other:
                    possibleMoves |= (self.getNeighbors((x, y), checkOccupied=True))
    
        return filter(lambda k: self.isValidPlay(k), possibleMoves)
                
    def isValidPlay(self, coor):
        """ Returns whether the move is a valid one. """
        other = self.other
        x, y = coor
        # Right, left, up, down, y = x left, y = x rigth, y = -x below, y = -x above
        otherFound = [False] * 8
        
        #Right
        if x < 7 and self.board[y][x + 1] == self.other:
            for x1 in xrange(x + 1, 8):
                if self.board[y][x1] == other:
                    otherFound[0] = True
                elif self.board[y][x1] == self.play and otherFound[0]:
                    return True
                else:
                    break
    
        #Left    
        if x > 0 and self.board[y][x - 1] == self.other:
            for x1 in xrange(x - 1, -1, -1):
                if self.board[y][x1] == other:
                    otherFound[1] = True
                elif self.board[y][x1] == self.play and otherFound[1]:
                    return True
                else:
                    break    
    
        #Up
        if y > 0 and self.board[y - 1][x] == self.other:
            for y1 in xrange(y - 1, -1, -1):
                if self.board[y1][x] == other:
                    otherFound[2] = True
                elif self.board[y1][x] == self.play and otherFound[2]:
                    return True
                else:
                    break          
            
        #Down
        if y < 7 and self.board[y + 1][x] == self.other:
            for y1 in xrange(y + 1, 8):
                if self.board[y1][x] == other:
                    otherFound[3] = True
                elif self.board[y1][x] == self.play and otherFound[3]:
                    return True
                else:
                    break                
        #Y = x right   
        for inc in xrange(1, 8 - max(x, y) - 1):
            if self.board[y - inc][x + inc] == other:
                otherFound[4] = True
            elif self.board[y - inc][x + inc] == self.play and otherFound[4]:
                    return True
                else:
                    break        
        #Y = x left
        for inc in xrange(0, min(x, y)):
            if self.board[y + inc][x - inc] == other:
                otherFound[5] = True
            elif self.board[y + inc][x - inc] == self.play and otherFound[5]:
                    return True
                else:
                    break  
        #Y = -x left
        for inc in xrange(0, min(x, y)):
            if self.board[y - inc][x - inc] == other:
                otherFound[6] = True
            elif self.board[y - inc][x - inc] == self.play and otherFound[6]:
                    return True
                else:
                    break       
        #Y = -x right
        for inc in xrange(1, 8 - max(x, y) - 1):
            if self.board[y + inc][x + inc] == other:
                otherFound[7] = True
            elif self.board[y + inc][x + inc] == self.play and otherFound[7]:
                    return True
                else:
                    break       
        return False
    def playMove(self, coor):
        """ Play's the move. """
        other = self.other
        x, y = coor
        self.board[y][x] = self.play
        otherFound = [False] * 8
        terminatorFound = [False] * 8
        #Check if change
        #Right
        for x1 in xrange(x + 1, 8):
            if self.board[y][x1] == other:
                otherFound[0] = True
            elif self.board[y][x1] == self.play and otherFound[0]:
                terminatorFound[0] = True
                break
            else:
                break            
        #Left
        for x1 in xrange(x - 1, -1, -1):
            if self.board[y][x1] == other:
                otherFound[1] = True
            elif self.board[y][x1] == self.play and otherFound[1]:
                terminatorFound[1] = True
                break    
            else:
                break            
        #Up
        for y1 in xrange(y - 1, -1, -1):
            if self.board[y1][x] == other:
                otherFound[2] = True
            elif self.board[y1][x] == self.play and otherFound[2]:
                terminatorFound[2] = True
                break
            else:
                break            
        #Down
        for y1 in xrange(y + 1, 8):
            if self.board[y1][x] == other:
                otherFound[3] = True
            elif self.board[y1][x] == self.play and otherFound[3]:
                terminatorFound[3] = True
                break  
            else:
                break            
        #Y = x right   
        for inc in xrange(1, 8 - max(x, y) - 1):
            if self.board[y - inc][x + inc] == other:
                otherFound[4] = True
            elif self.board[y - inc][x + inc] == self.play and otherFound[4]:
                terminatorFound[4] = True
                break   
            else:
                break            
        #Y = x left
        for inc in xrange(0, min(x, y)):
            if self.board[y + inc][x - inc] == other:
                otherFound[5] = True
            elif self.board[y + inc][x - inc] == self.play and otherFound[6]:
                terminatorFound[6] = True
                break   
            else:
                break            
        #Y = -x left
        for inc in xrange(0, min(x, y)):
            if self.board[y - inc][x - inc] == other:
                otherFound[6] = True
            elif self.board[y - inc][x - inc] == self.play and otherFound[6]:
                terminatorFound[6] = True
                break   
            else:
                break            
        #Y = -x right
        for inc in xrange(1, 8 - max(x, y) - 1):
            if self.board[y + inc][x + inc] == other:
                otherFound[7] = True
            elif self.board[y + inc][x + inc] == self.play and otherFound[7]:
                terminatorFound[7] = True
                break 
            else:
                break            
           
    
    
        
        #Flip
        #Right
        if terminatorFound[0]:
            for x1 in xrange(x + 1, 8):
                if self.board[y][x1] == other:
                    self.board[y][x1] = self.play
                elif self.board[y][x1] == self.play:
                    break
        #Left
        if terminatorFound[1]:
            for x1 in xrange(x - 1, -1, -1):
                if self.board[y][x1] == other:
                    self.board[y][x1] = self.play
                elif self.board[y][x1] == self.play:
                    break        
        #Up
        if terminatorFound[2]:
            for y1 in xrange(y - 1, -1, -1):
                if self.board[y1][x] == other:
                    self.board[y1][x] = self.play
                elif self.board[y1][x] == self.play:
                    break
        #Down
        if terminatorFound[3]:
            for y1 in xrange(y + 1, 8):
                if self.board[y1][x] == other:
                    self.board[y1][x] = self.play
                elif self.board[y1][x] == self.play:
                    break      
        #Y = x right
        if terminatorFound[4]:
            for inc in xrange(1, 8 - max(x, y) - 1):
                if self.board[y - inc][x + inc] == other:
                    self.board[y - inc][x + inc] = self.play
                elif self.board[y - inc][x + inc] == self.play:
                    break    
        #Y = x left
        if terminatorFound[5]:
            for inc in xrange(0, min(x, y)):
                if self.board[y + inc][x - inc] == other:
                    self.board[y + inc][x - inc] = self.play
                elif self.board[y + inc][x - inc] == self.play:
                    break           
        #Y = x left
        if terminatorFound[6]:
            for inc in xrange(0, min(x, y)):
                if self.board[y - inc][x - inc] == other:
                    self.board[y - inc][x - inc] = self.play
                elif self.board[y - inc][x - inc] == self.play:
                    break    
        #Y = x right
        if terminatorFound[7]:
            for inc in xrange(1, 8 - max(x, y) - 1):
                if self.board[y + inc][x + inc] == other:
                    self.board[y + inc][x + inc] = self.play
                elif self.board[y + inc][x + inc] == self.play:
                    break     
    def playGame(self, turns):
        """ Sets the AI against itself. """
        self.printBoard()
    
        for i in xrange(turns):
            self.playMove(self.bestMove["coord"])
            print self.bestMove["coord"], self.play
            self.origBoard = [row[:] for row in self.board]
            self.printBoard()
            self.play, self.other = self.other, self.play
            self.getBestMove()
            
def main():
    #action=?? outputfile=?? result_prefix=?? ply=?? play=?? board=?? cputime=??
    othellogame = Othello(depth=2)
    othellogame.playGame(50)

if __name__ == "__main__":
    main()