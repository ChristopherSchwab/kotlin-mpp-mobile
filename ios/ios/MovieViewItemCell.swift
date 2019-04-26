//
//  MovieViewItemCell.swift
//  ios
//
//  Created by Christopher Schwab on 25.04.19.
//  Copyright © 2019 Christopher Schwab. All rights reserved.
//

import common
import UIKit
import SDWebImage

class MovieViewItemCell: UICollectionViewCell {

   @IBOutlet var ivPoster: UIImageView!
   @IBOutlet var lblTitle: UILabel!
   @IBOutlet var lblReleaseDate: UILabel!
   
   func present(_ movie: MovieViewItem) {
      lblTitle.text = movie.title
      lblReleaseDate.text = movie.releaseDate
      if let posterUrl = movie.posterUrl {
         ivPoster.sd_setImage(with: URL(string: posterUrl), placeholderImage: UIImage(named: "placeholder"))
      }
   }
}
