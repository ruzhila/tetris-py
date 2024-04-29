import tkinter as tk
import random
shapes = [
    [[1, 1, 1, 1]],                             # I shape
    [[1, 1], [1, 1]],                           # O shape
    [[1, 1, 0], [0, 1, 1], [0, 0, 0]],          # Z shape
    [[0, 1, 1], [1, 1]],                        # S shape
    [[1, 1, 1], [0, 1, 0]],                     # T shape
    [[0, 1, 1], [1, 1, 0]],                     # L shape
    [[1, 1, 1], [0, 0, 1]]                      # J shape
]


class Tetris(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title('Tetris by ruzhila.cn')
        self.canvas = tk.Canvas(self, width=200, height=400, bg='white')
        self.canvas.pack()
        self.board = [[0] * 10 for _ in range(20)]
        self.score, self.game_over = 0, False
        self.new_shape()
        self.bind("<Key>", self.key_press)
        self.after(1000, self.fall)

    def key_press(self, event):
        if self.can_move(0, 1):
            if event.keysym == 'Up':
                self.shape = list(zip(*reversed(self.shape)))
            elif event.keysym == 'Down':
                self.shape_y += 1
        if event.keysym == 'Left' and self.can_move(-1, 0):
            self.shape_x -= 1
        elif event.keysym == 'Right' and self.can_move(1, 0):
            self.shape_x += 1
        self.redraw()

    def new_shape(self):
        self.shape = random.choice(shapes)
        self.shape_x = 5
        self.shape_y = 0
        self.color = random.choice(['blue', 'green', 'yellow', 'pink'])

    def fall(self):
        self.game_over = not self.can_move(0, 0)
        if not self.game_over:
            if self.can_move(0, 1):
                self.shape_y += 1
            else:
                self.add_to_board()
                self.check_lines()
                self.new_shape()
            self.after(1000, self.fall)
        self.redraw()

    def can_move(self, dx, dy):
        for i, row in enumerate(self.shape):
            for j, cell in enumerate(row):
                if cell:
                    x = self.shape_x + j + dx
                    y = self.shape_y + i + dy
                    if x < 0 or x >= 10 or y < 0 or y >= 20 or self.board[y][x]:
                        return False
        return True

    def add_to_board(self):
        for i, row in enumerate(self.shape):
            for j, cell in enumerate(row):
                if cell:
                    self.board[self.shape_y + i][self.shape_x + j] = 1

    def check_lines(self):
        for i, row in enumerate(self.board):
            if all(cell for cell in row):
                del self.board[i]
                self.board.insert(0, [0] * 10)
                self.score += 1

    def draw_shape(self, shape, shape_x, shape_y, color):
        for i, row in enumerate(shape):
            for j, cell in enumerate(row):
                if cell:
                    x = (shape_x + j) * 20
                    y = (shape_y + i) * 20
                    self.canvas.create_rectangle(
                        x, y, x + 20, y + 20, fill=color, outline='gray')

    def redraw(self):
        self.canvas.delete('all')
        self.draw_shape(self.board, 0, 0, 'lightgray')
        self.draw_shape(self.shape, self.shape_x, self.shape_y, self.color)
        if self.game_over:
            self.canvas.create_text(100, 200, anchor='center',
                                    text='Game Over', fill='red', font=('Arial', 20))
        self.canvas.create_text(10, 10, anchor='nw',
                                text=f'Score: {self.score} | ruzhila.cn', fill='blue')


if __name__ == '__main__':
    Tetris().mainloop()
