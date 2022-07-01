import pygame

# type, number, color
coin_info = (
    ('mille', 1, (230, 230, 230)),
    ('mille', 2, (230, 230, 230)),
    ('mille', 5, (230, 230, 230)),
    ('cent', 1, (191, 191, 191)),
    ('cent', 5, (191, 191, 191)),
    ('cent', 10, (191, 191, 191)),
    ('cent', 25, (191, 191, 191)),
    ('cent', 50, (191, 191, 191)),
    ('dollar', 1, (255, 255, 0)),
    ('dollar', 5, (255, 255, 0)),
    ('dollar', 10, (255, 255, 0)),
    ('dollar', 20, (255, 255, 0)),
)

# number, color
bill_info = (
    (1, (255, 127, 0)),
    (5, (255, 191, 0)),
    (10, (255, 255, 0)),
    (20, (191, 255, 0)),
    (50, (127, 255, 0)),
    (100, (63, 255, 0)),
    (200, (0, 255, 0)),
    (500, (0, 255, 63)),
    (1000, (0, 255, 127)),
    (2000, (0, 255, 191)),
    (5000, (0, 255, 255)),
    (10000, (0, 191, 255)),
    (20000, (0, 127, 255)),
    (50000, (0, 63, 255)),
    (100000, (0, 0, 255)),
    (200000, (63, 0, 255)),
    (500000, (127, 0, 255)),
    (1000000, (191, 0, 255)),
)

pygame.font.init()
font = pygame.font.SysFont('Arial', 30)

for coin_type, coin_number, coin_color in coin_info:
    coin = pygame.Surface((64, 64), pygame.SRCALPHA)
    
    coin.fill((0, 0, 0, 0))
    
    pygame.draw.circle(coin, (*coin_color, 255), (32, 32), 32)
    
    font_surf = font.render(str(coin_number), False, (0, 0, 0, 255), (255, 255, 255, 0))
    
    coin.blit(font_surf, (33 - font_surf.get_width() / 2, 32 - font_surf.get_height() / 2), pygame.Rect(0, 0, font_surf.get_width(), font_surf.get_height()), pygame.BLEND_MIN)
    
    pygame.image.save(coin, f'../src/main/resources/assets/cgcryos_money_mod/textures/items/coin_{coin_number}_{coin_type}.png')

for bill_amount, bill_color in bill_info:
    bill = pygame.Surface((64, 64), pygame.SRCALPHA)
    
    bill.fill((0, 0, 0, 0), pygame.Rect(0, 0, 64, 10))
    bill.fill((0, 0, 0, 0), pygame.Rect(0, 48, 64, 10))
    
    bill.fill((*bill_color, 255), pygame.Rect(0, 10, 64, 44))
    
    bill.fill((255, 255, 255, 255), pygame.Rect(8, 14, 48, 36))
    
    bill_amount_str = str(bill_amount)
    
    if bill_amount < 1000:
        bill_text = bill_amount_str
    else:
        bill_text = bill_amount_str[0] + 'E' + str(len(bill_amount_str) - 1)
    
    font_surf = font.render(bill_text, False, (0, 0, 0, 255), (255, 255, 255, 0))
    
    bill.blit(font_surf, (33 - font_surf.get_width() / 2, 32 - font_surf.get_height() / 2), pygame.Rect(0, 0, font_surf.get_width(), font_surf.get_height()), pygame.BLEND_MIN)
    
    pygame.image.save(bill, f'../src/main/resources/assets/cgcryos_money_mod/textures/items/bill_{bill_amount}_dollar.png')
